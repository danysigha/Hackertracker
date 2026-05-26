# 🎯 HackerTracker

A comprehensive LeetCode progress tracking and intelligent scheduling platform designed to help you master the **LeetCode Top 150 Questions**. HackerTracker combines progress tracking, smart scheduling, and a rules based recommendation system to optimize your coding interview preparation.

## Features

### 📊 Progress Tracking

- **Problem Completion Logging**: Track which LeetCode Top 150 questions you've completed with detailed notes
- **Performance Metrics**: Monitor your progress across difficulty levels (Easy, Medium, Hard) and topics (Arrays, Strings, Trees, etc.)
- **Completion Rate Analytics**: Visualize your journey with an eye-opening progress dashboard.

### ⏱️ Time Management

- **Session Timing**: Time your problem-solving sessions to understand your pace and efficiency
- **Historical Tracking**: Review your progress history to identify improvement areas

### 📅 Smart Scheduling

- **Personalized Schedules**: Create custom study schedules based on your target timeline and availability
- **Rules-based Recommender**: The scheduler analyzes:
  - How you rank questions (difficulty perception vs. actual difficulty)
  - Your completion history by topic and difficulty
  - Your learning pace and patterns
  - Available time slots
- **Dynamic Recommendations**: Receive personalized next-question suggestions to optimize your preparation

### 👥 User Management

- **Secure Registration**: Create accounts with password authentication
- **User Profiles**: Customize your learning preferences and goals
- **Progress Persistence**: All your data is saved and synchronized across sessions

### 🔍 Advanced Search

- **Full-Text Search**: Search problems by title, description, and tags using Hibernate Search
- **Topic Filtering**: Browse and filter questions by topic and difficulty
- **Smart Tagging**: Organize problems with custom tags for better organization

## Screenshots

|                                                                |                                                                    |
| -------------------------------------------------------------- | ------------------------------------------------------------------ |
| ![Main Dashboard - Light](docs/screenshots/main_dashboard.png) | ![Main Dashboard - Dark](docs/screenshots/main_dashboard_dark.png) |
| **Light Mode**                                                 | **Dark Mode**                                                      |
| ![Progress Dashboard](docs/screenshots/progress_dashboard.png) | ![Progress Analytics](docs/screenshots/progress_dashboard_bis.png) |
| **Progress Dashboard**                                         | **Progress Analytics**                                             |

## Tech Stack

| Component             | Technology                       |
| --------------------- | -------------------------------- |
| **Backend Framework** | Spring Boot 3.4.4                |
| **Java Version**      | Java 21                          |
| **Security**          | Spring Security with JWT         |
| **Database**          | MySQL                            |
| **ORM**               | Hibernate 6.6.13                 |
| **Search Engine**     | Hibernate Search (Apache Lucene) |
| **Caching**           | EHCache with JCache              |
| **Frontend**          | JSP with HTML/CSS/JavaScript     |
| **Styling**           | SCSS                             |
| **Build Tool**        | Maven                            |

## Prerequisites

- **Docker** ([Download](https://www.docker.com/products/docker-desktop))
- **Docker Compose** (included with Docker Desktop)
- **Git** ([Download](https://git-scm.com/))

---

## 📖 For More Information

For detailed setup instructions, validation rules, testing, database management, troubleshooting, and more, see [DETAILED_GUIDE.md](DETAILED_GUIDE.md).

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues, questions, or suggestions:

- Open an issue on GitHub
- Check existing issues for similar problems
- Provide detailed error messages and logs when reporting bugs

---

**Happy coding! Good luck with your LeetCode preparation! 🚀**
