# Simple Java Mail App

This repository contains a simple Mail app written in Java, utilizing Swing and AWT for the graphical user interface. Please note that the code does not adhere to best practices such as MVC, DRY, or clean code principles. It is a project that reflects the author's learning journey and may not be suitable for production use.

## Features

- **Login Screen**: Users enter their credentials to connect to the mail server for sending and receiving emails.

- **Credential Verification**: The app immediately checks the entered credentials by connecting to the server to perform a login.
## Main Menu

The application's main menu provides several functionalities, connecting seamlessly between the email server and a JDBC Apache Derby embedded database.

- **"Empfangen" (Receive)**: Clicking this button opens a window featuring a TableView. The TableView is connected to both the email server and the JDBC database, providing updates on received emails. The application connects to the mail server, retrieves email data, and updates the TableView accordingly. If new emails exist, a dialog window displays them; otherwise, a notification informs the user.

  - **Toolbar Options**:
    - **Reply**: Respond to selected emails.
    - **Forward**: Forward selected emails.

- **"Senden" (Send)**: Clicking this button opens a window with a TableView, displaying sent emails stored in the JDBC database. The TableView window also features a Toolbar with additional functionality:

  - **Toolbar Options**:
    - **Compose**: Opens a new window to compose and send new emails.

- **Quit Button**: Exits the application.

This interconnected setup ensures a seamless flow of data between the email server and the local JDBC database, allowing users to efficiently manage their emails within the application.

## Icons

The project includes custom icons, created by the author. Please note that the visual design may not meet professional standards.

## Coding Style

The coding style is straightforward, reflecting an experienced novice project. The lack of adherence to design patterns may make refactoring challenging.

## Technical Details

- **Language**: Java 21
- **GUI Frameworks**: Swing and AWT
- **Build Tool**: Maven
- **Database**: Apache Derby embedded v10.16.1.1
- **Mail Library**: Eclipse Angus-Mail 2.0.0
- **Styling**: No CSS styling applied
- **Icons**: Custom, with a simple design

## How to Use

1. Clone the repository.
2. Open the project in your preferred Java IDE.
3. Run the application.

## Database

The app opens the embedded Derby database `mailprojectDB` relative to the working directory
(`src/main/java/de/veloper/remote/Constants.java`). It expects the tables `empfangen` (received)
and `gesendet` (sent).

## Note

This project is a learning experiment and may not be suitable for production. Feel free to explore, experiment, and contribute to its improvement.

## License

MIT, see [LICENSE](LICENSE).

## Contact

Johnny-tech-droid · contact@hausrund.info · https://github.com/Johnny-tech-droid
