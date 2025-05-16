from reportlab.pdfgen import canvas
from reportlab.lib.pagesizes import letter
import os

def generate_booking_pdf(booking, user, path):
    c = canvas.Canvas(path, pagesize=letter)
    c.setFont("Helvetica", 14)
    c.drawString(100, 750, "Home Service Booking Confirmation")
    c.setFont("Helvetica", 12)
    c.drawString(100, 720, f"Booking ID: {booking.id}")
    c.drawString(100, 700, f"Customer: {user.fullname}")
    c.drawString(100, 680, f"Email: {user.email}")
    c.drawString(100, 660, f"Service Type: {booking.service_type}")
    c.drawString(100, 640, f"Preferred Date: {booking.date}")
    c.drawString(100, 620, f"Status: {booking.status}")
    if booking.description:
        c.drawString(100, 600, "Description:")
        text = c.beginText(100, 580)
        text.textLines(booking.description)
        c.drawText(text)
    c.save()
