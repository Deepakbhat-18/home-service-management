from flask import Blueprint, render_template, request, redirect, url_for, flash, session, send_from_directory
from .models import User, ServiceBooking, Service, db
from fpdf import FPDF
from datetime import datetime
import os

main = Blueprint('main', __name__)

@main.route('/')
def home():
    return render_template('index.html', user_name=session.get('user_name'))

@main.route('/register', methods=['GET', 'POST'])
def register():
    if request.method == 'POST':
        email = request.form['email']
        fullname = request.form['fullname']
        address = request.form['address']
        phone = request.form['phone']
        role = request.form['role']
        password = request.form['password']

        if User.query.filter_by(email=email).first():
            flash('Email already registered')
            return redirect(url_for('main.register'))

        user = User(email=email, fullname=fullname, address=address,
                    phone=phone, role=role, password=password)
        db.session.add(user)
        db.session.commit()

        flash('Registration successful. Please log in.')
        return redirect(url_for('main.login'))

    return render_template('register.html')

@main.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        email = request.form['email']
        password = request.form['password']
        user = User.query.filter_by(email=email).first()

        if user and user.password == password:
            session['user_id'] = user.id
            session['user_name'] = user.fullname
            flash('Login successful.')
            return redirect(url_for('main.dashboard'))
        else:
            flash('Invalid email or password')

    return render_template("login.html")

@main.route('/logout')
def logout():
    session.clear()
    flash('You have been logged out.')
    return redirect(url_for('main.login'))

@main.route('/dashboard')
def dashboard():
    user_id = session.get('user_id')
    if not user_id:
        flash('Please log in to view the dashboard.')
        return redirect(url_for('main.login'))

    user = User.query.get(user_id)
    bookings = []

    if user.role == 'customer':
        bookings = ServiceBooking.query.filter_by(customer_id=user.id).all()

    return render_template('dashboard.html', user=user, bookings=bookings)

@main.route('/browse-services')
def browse_services():
    services = Service.query.all()
    return render_template('browse_services.html', services=services)

@main.route('/book-service', methods=['GET', 'POST'])
def book_service():
    user_id = session.get('user_id')
    if not user_id:
        flash('Please log in to book a service.')
        return redirect(url_for('main.login'))

    user = User.query.get(user_id)
    if user.role != 'customer':
        flash('Only customers can book services.')
        return redirect(url_for('main.dashboard'))

    if request.method == 'POST':
        service_type = request.form['service_type']
        description = request.form['description']
        date = request.form['date']

        providers = User.query.filter_by(role='provider').all()
        provider_assignments = {
            provider.id: ServiceBooking.query.filter_by(provider_id=provider.id).count()
            for provider in providers
        }
        provider_id = min(provider_assignments, key=provider_assignments.get) if provider_assignments else None

        booking = ServiceBooking(
            customer_id=user.id,
            provider_id=provider_id,
            service_type=service_type,
            description=description,
            date=date,
            status='Pending'
        )
        db.session.add(booking)
        db.session.commit()

        flash("Service booked successfully and assigned to a provider.")
        return redirect(url_for('main.dashboard'))

    prefill_service = request.args.get('service_type', '')
    return render_template('book_service.html', prefill_service=prefill_service)

@main.route('/generate-bill/<int:booking_id>')
def generate_bill(booking_id):
    user_id = session.get('user_id')
    if not user_id:
        flash("Please log in to generate a bill.")
        return redirect(url_for('main.login'))

    user = User.query.get(user_id)
    booking = ServiceBooking.query.get_or_404(booking_id)
    customer = User.query.get(booking.customer_id)

    if user.role not in ['admin', 'provider'] and booking.customer_id != user.id:
        flash("Unauthorized access.")
        return redirect(url_for('main.dashboard'))

    pdf_dir = os.path.join(os.getcwd(), "app", "pdfs")
    os.makedirs(pdf_dir, exist_ok=True)

    pdf_filename = f"Invoice_{booking.id}.pdf"
    pdf_path = os.path.join(pdf_dir, pdf_filename)

    pdf = FPDF()
    pdf.add_page()
    pdf.set_font("Arial", "B", 16)
    pdf.cell(0, 10, "Service Invoice", ln=True, align="C")

    pdf.set_font("Arial", "", 12)
    pdf.ln(10)
    pdf.cell(0, 10, f"Date: {datetime.now().strftime('%Y-%m-%d %H:%M')}", ln=True)
    pdf.cell(0, 10, f"Invoice ID: {booking.id}", ln=True)
    pdf.ln(5)
    pdf.cell(0, 10, "Customer Details:", ln=True)
    pdf.cell(0, 10, f"Name: {customer.fullname}", ln=True)
    pdf.cell(0, 10, f"Address: {customer.address}", ln=True)
    pdf.cell(0, 10, f"Phone: {customer.phone}", ln=True)
    pdf.cell(0, 10, f"Email: {customer.email}", ln=True)
    pdf.ln(5)
    pdf.cell(0, 10, "Service Details:", ln=True)
    pdf.cell(0, 10, f"Service Type: {booking.service_type}", ln=True)
    pdf.cell(0, 10, f"Description: {booking.description}", ln=True)
    pdf.cell(0, 10, f"Service Date: {booking.date}", ln=True)
    pdf.cell(0, 10, f"Status: {booking.status}", ln=True)
    pdf.cell(0, 10, "Amount: Rs. 500", ln=True)

    pdf.output(pdf_path)

    flash("Invoice generated successfully.")
    return send_from_directory(directory=pdf_dir, path=pdf_filename, as_attachment=True)

@main.route('/provider-dashboard', methods=['GET', 'POST'])
def provider_dashboard():
    user_id = session.get('user_id')
    if not user_id:
        flash('Please log in first.')
        return redirect(url_for('main.login'))

    user = User.query.get(user_id)
    if user.role != 'provider':
        flash('Access denied.')
        return redirect(url_for('main.dashboard'))

    if request.method == 'POST':
        booking_id = request.form['booking_id']
        new_status = request.form['status']
        booking = ServiceBooking.query.get(booking_id)
        if booking and booking.provider_id == user.id:
            booking.status = new_status
            db.session.commit()
            flash('Booking status updated.')
        return redirect(url_for('main.provider_dashboard'))

    bookings = ServiceBooking.query.filter_by(provider_id=user.id).all()
    return render_template('provider_bookings.html', user=user, bookings=bookings)

@main.route('/admin-dashboard', methods=['GET', 'POST'])
def admin_dashboard():
    user_id = session.get('user_id')
    if not user_id:
        flash('Please log in.')
        return redirect(url_for('main.login'))

    user = User.query.get(user_id)
    if user.role != 'admin':
        flash('Access denied.')
        return redirect(url_for('main.dashboard'))

    if request.method == 'POST':
        booking_id = request.form['booking_id']
        provider_id = request.form['provider_id']
        booking = ServiceBooking.query.get(booking_id)
        provider = User.query.get(provider_id)
        if booking and provider and provider.role == 'provider':
            booking.provider_id = provider.id
            db.session.commit()
            flash('Provider assigned successfully.')
        return redirect(url_for('main.admin_dashboard'))

    bookings = ServiceBooking.query.all()
    users = User.query.all()
    providers = [u for u in users if u.role == 'provider']
    return render_template('admin_dashboard.html', bookings=bookings, users=users, providers=providers)
