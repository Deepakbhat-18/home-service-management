from flask import Flask
from flask_sqlalchemy import SQLAlchemy

app = Flask(__name__)
app.config['SECRET_KEY'] = 'your-secret-key'
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://root:Drb2004.@localhost/home_service'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db = SQLAlchemy(app)

class Service(db.Model):
    __tablename__ = 'services'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    description = db.Column(db.String(100))
    price = db.Column(db.Numeric(10, 2), nullable=False)

with app.app_context():
    db.create_all()

    # Optional: clear old services
    Service.query.delete()

    service_names = [
        "Plumbing", "Electrical Repair", "House Cleaning", "AC Maintenance", "Carpentry",
        "Painting", "Pest Control", "Appliance Repair", "Gardening", "Laundry Service",
        "Car Wash", "Home Security Installation", "Solar Panel Cleaning", "Window Cleaning",
        "Gutter Cleaning", "Internet Setup", "Furniture Assembly", "Babysitting",
        "Pet Grooming", "CCTV Installation"
    ]

    for i, name in enumerate(service_names, start=1):
        service = Service(
            name=name,
            description=f"This is the description for {name}.",
            price=100 + i * 10
        )
        db.session.add(service)

    db.session.commit()
    print("✅ 20 realistic services added successfully.")
