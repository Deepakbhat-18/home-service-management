# Home Service Management System

Home Service Management System is a CRUD-based full-stack web application that helps users book home services and manage service requests based on roles.
The application is built to demonstrate core backend concepts, database operations, and frontend–backend integration.

## Installation

Install the project locally using Python and pip.

git clone <repository-url>
cd home-service-management
pip install -r requirements.txt

This installs all required Python libraries needed to run the Flask application.

## Usage

Start the Flask development server:

python run.py

Then open your browser and navigate to:

http://127.0.0.1:5000

Users can register, log in, and access features based on their assigned role.

## Features

### User Authentication
Users can register and log in using email and password.

### Role-Based Access Control
The system supports three roles:
- Customer
- Service Provider
- Admin

### Home Service Booking (CRUD)
Customers can create, view, and track home service bookings (e.g., plumbing, electrical, cleaning).

### Provider Assignment
Service providers are automatically assigned based on availability.

### Booking Status Management
Providers can update the status of assigned service requests.

### Invoice Generation
A PDF invoice is generated for each completed service using FPDF.

## Tech Stack

Backend   : Flask, SQLAlchemy  
Frontend  : HTML, CSS, JavaScript  
Database  : SQLite / PostgreSQL  
Utilities : FPDF  

## Example

### Customer books a home service
create_booking(service_type, description, date)

### Provider updates service status
update_status(booking_id, "Completed")

### Admin assigns provider manually
assign_provider(booking_id, provider_id)

## Project Type

This project is intended as a learning-focused CRUD application to understand:
- Database operations
- Backend routing
- Role-based workflows
- Frontend–backend communication

## Contributing

Contributions are welcome.
Please open an issue before making major changes and ensure code is tested properly.
