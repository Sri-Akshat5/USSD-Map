# USSD-Based Navigation System 
A telecom-grade USSD application that provides step-by-step directions between two locations—optimized for **low-internet or offline environments**.

## Features 
- 📍 Location-to-location navigation via **Google Maps Directions API**
- 🗺️ Step-by-step **paginated USSD flow**
- 📩 SMS fallback using **Twilio** APIs
- 🔄 Session tracking and retry support
- ✅ Built for feature phones and low-data users
---

## Tech Stack
| Layer        | Technology           |
|--------------|----------------------|
| Backend      | Spring Boot (Java 17)   |
| API Services | Google Maps API, Twilio, Africa's Talking |
| Database     | In-memory / MySQL (configurable) |
| Deployment   | Ngrok (local), Render/Heroku (cloud) |
| USSD Gateway | Africa's Talking     |

## Configure Environment
Create or update **application.properties** and set the following fields

### Google Maps
- google.api.key=YOUR_GOOGLE_API_KEY

### Database 
- spring.datasource.url=jdbc:mysql://localhost:3306/ussd_map
- spring.datasource.username=root
- spring.datasource.password=<PASSWORD>
- spring.jpa.hibernate.ddl-auto=update
- spring.jpa.show-sql=true

### Twilio in TwilioSmsService.java
- twilio.account.sid=YOUR_TWILIO_SID
- twilio.auth.token=YOUR_TWILIO_TOKEN
- twilio.phone.number=+1XXXXXXXXXX

## Run the application 
- ./mvnw spring-boot:run

### 🙋‍♂️ Author
**Akshat Srivastava**
Reach out on [LinkedIn](linkedin.com/in/sriakshat5/) or email: akshatsrivastava566@gmail.com

