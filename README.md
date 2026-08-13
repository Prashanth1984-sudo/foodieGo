# 🍔 FoodieGo — Full Stack Food Delivery Platform

FoodieGo is a full-stack food delivery web application built with **Spring Boot** and **Thymeleaf**, designed to replicate the core ordering experience of platforms like Swiggy and Zomato. It covers the complete order lifecycle — from browsing restaurants and menus to secure checkout, live order tracking, and admin-side management — with a strong focus on clean UI/UX and production-ready features.

---

## ✨ Features

- 🔐 **Secure Authentication** — Spring Security-based login/signup with email OTP verification via Spring Mail
- 🛒 **Cart & Checkout** — Two-column, Swiggy/Zomato-style checkout and order details pages
- 🏷️ **Coupons & Discounts** — Dynamic coupon calculation applied at checkout
- 💳 **Online Payments** — Integrated UPI/card payments through **Razorpay**
- 📦 **Order Tracking** — Real-time order status and detailed order history
- 🛠️ **Admin Dashboard** — Full admin panel to manage restaurants, menu items, orders, and users
- 🎬 **Cinematic UI** — Canvas-based animated hero section and interactive cooking-scene widget for a premium landing experience
- 📱 **Responsive Design** — Optimized layouts across devices

---

## 🧰 Tech Stack

| Layer            | Technology                          |
|-------------------|--------------------------------------|
| Backend           | Java, Spring Boot                    |
| Templating        | Thymeleaf                            |
| Security          | Spring Security                      |
| Email/OTP         | Spring Mail                          |
| Payments          | Razorpay API (UPI & Card)            |
| Database          | MySQL                                |
| Frontend          | HTML5, CSS3, JavaScript, Canvas API  |
| Build Tool        | Maven                                |

> Adjust the database and build tool entries above if your setup differs.

---

## 🏗️ Architecture

FoodieGo follows a layered MVC architecture:

```
Controller → Service → Repository → Database
      ↓
  Thymeleaf Views (Frontend)
```

An entity-relationship diagram covering Users, Restaurants, Menu Items, Orders, and Payments is available in the `/docs` folder (see project presentation for the full ER diagram).

---

## 📸 Screenshots

> Add screenshots or a demo GIF here — landing page, checkout flow, and admin dashboard work well to showcase the UI.

```
docs/screenshots/landing.png
docs/screenshots/checkout.png
docs/screenshots/admin-dashboard.png
```

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven
- MySQL 8+
- A Razorpay account (test API keys) for payment integration
- SMTP credentials (e.g., Gmail App Password) for OTP emails

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/<your-username>/foodiego.git
   cd foodiego
   ```

2. **Configure application properties**

   Create `src/main/resources/application.properties` (do **not** commit real credentials):
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/foodiego_db
   spring.datasource.username=your_db_username
   spring.datasource.password=your_db_password

   spring.mail.username=your_email@gmail.com
   spring.mail.password=your_app_password

   razorpay.key=your_razorpay_key
   razorpay.secret=your_razorpay_secret
   ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. Visit `http://localhost:8080` in your browser.

> ⚠️ **Security note:** Never commit real API keys or database credentials. Use environment variables or a `.gitignore`'d properties file, and rotate any keys that may have been exposed during development.

---

## 📂 Project Structure

```
foodiego/
├── src/
│   ├── main/
│   │   ├── java/com/foodiego/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── entity/
│   │   │   └── config/
│   │   └── resources/
│   │       ├── templates/      # Thymeleaf views
│   │       ├── static/         # CSS, JS, images
│   │       └── application.properties
├── pom.xml
└── README.md
```

---

## 🗺️ Roadmap

- [ ] Restaurant owner self-service portal
- [ ] Real-time order tracking with WebSockets
- [ ] Ratings & reviews module
- [ ] Multi-language support

---

## 🤝 Contributing

Contributions are welcome! Feel free to fork the repo, open an issue, or submit a pull request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

## 📬 Contact

**Prashanth**
Full Stack Java Developer | Bengaluru, India
📧 Reach out via [LinkedIn](https://linkedin.com) or GitHub Issues for questions and feedback.

---

<p align="center">Made with ☕ and Spring Boot</p>
