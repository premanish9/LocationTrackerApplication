This is News application 

-->To run this application you need to select newsapp branch and then run code .




Below is documentation:-

                                                                                                            News App
OVERVIEW
Develop a news application using Android Native with Kotlin/Java, implemented using either XML-based UI
or Jetpack Compose.


The app will have two screens:
1. News List Screen:
○
○
Displays a list of news articles,
Each item should show a thumbnail image (if available), title, and a brief description.
2. News Details Screen:
○
○
Displays the full details of a selected news article
Includes the title, description, image (if available), and possibly the full article text
(depending on the API response).
API Details:
●
●
Use the News API: https://newsapi.org/.
Obtain an API key for fetching news data.
Specifications:
1. Online/Oﬄine Functionality
●
●
The app should initially require an internet connection to fetch and cache news data.
Subsequent usage should work oﬄine, displaying the cached data.
2. State Management
●
Use appropriate Android state management techniques, such as ViewModel and LiveData/Flow.
3. Data Persistence
●
●
Store fetched news data locally using Room Persistence Library for oﬄine access.
Design an appropriate schema for storing news articles (using a data class).
4. Error Handling
●
●
Implement robust error handling for network requests and data persistence.
Display user-friendly error messages.
5. UI/UX
●
●
Design a visually appealing and user-friendly interface.
Pay attention to layout, typography, and visual hierarchy.
Bonus Points:
●
Modular Code: Organize your code into well-defined modules (e.g., network layer, data layer, UI
●
●
layer).
Clean Code: Follow best practices, including meaningful variable names, concise functions, and
proper code comments.
Unit Testing: Writing unit test cases will be a plus.
Submission:
●

