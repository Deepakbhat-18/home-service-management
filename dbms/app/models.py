from . import db

class User(db.Model):
    __tablename__ = 'users'

    id = db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String(120), unique=True, nullable=False)
    fullname = db.Column(db.String(100), nullable=False)
    address = db.Column(db.String(255), nullable=False)
    phone = db.Column(db.String(20), nullable=False)
    role = db.Column(db.String(20), nullable=False, default='customer')
    password = db.Column(db.String(128), nullable=False)  # plain-text password

class ServiceBooking(db.Model):
    __tablename__ = 'service_bookings'
    id = db.Column(db.Integer, primary_key=True)
    customer_id = db.Column(db.Integer, db.ForeignKey('users.id'))
    provider_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=True)
    service_type = db.Column(db.String(100))
    description = db.Column(db.Text)
    date = db.Column(db.String(100))
    status = db.Column(db.String(50), default='Pending')

    customer = db.relationship('User', foreign_keys=[customer_id])
    provider = db.relationship('User', foreign_keys=[provider_id])

class Service(db.Model):
    __tablename__ = 'services'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    description = db.Column(db.String(100))
    price = db.Column(db.Numeric(10, 2), nullable=False)

    def __repr__(self):
        return f'<service {self.name}>'
