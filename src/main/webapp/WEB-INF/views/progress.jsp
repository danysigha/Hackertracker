<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<!DOCTYPE html>
<html lang="en">
<head>
    <link rel="stylesheet" href="/css/baseStyle.css">
    <link rel="stylesheet" href="/css/progressStyle.css">
    <script type="module" src="/js/progressarc.js"></script>
    <script src="/js/timeseries.js"></script>
    <script src="/js/rankscript.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/3.9.1/chart.min.js"></script>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>HackerTracker</title>
</head>

<body>
<nav>
    <img class="logo" src="/assets/hackertracker.png" alt="HackerTracker Logo">

    <div class="navigation-buttons">

        <a href="/dashboard">
            <button>
                <svg class="icon" xmlns="http://www.w3.org/2000/svg" height="48px" viewBox="0 -960 960 960" width="48px" fill="#1f1f1f"><path d="M220-180h150v-250h220v250h150v-390L480-765 220-570v390Zm-60 60v-480l320-240 320 240v480H530v-250H430v250H160Zm320-353Z"/></svg>
            </button>
        </a>

        <a href="">
            <button>
                <svg class="icon" xmlns="http://www.w3.org/2000/svg" version="1.1" viewBox="5.0 1.0 90.0 90.0">
                    <path d="m50 27.707c-5.9141 0-11.582 2.3516-15.762 6.5312-4.1797 4.1797-6.5312 9.8477-6.5312 15.762s2.3516 11.582 6.5312 15.762c4.1797 4.1797 9.8477 6.5312 15.762 6.5312s11.582-2.3516 15.762-6.5312c4.1797-4.1797 6.5312-9.8477 6.5312-15.762-0.011719-5.9102-2.3633-11.574-6.543-15.75-4.1758-4.1797-9.8398-6.5312-15.75-6.543zm0 38.961c-4.3398-0.054688-8.4844-1.8242-11.527-4.918-3.043-3.0977-4.7383-7.2695-4.7227-11.609 0.019531-4.3398 1.7539-8.5 4.8203-11.57 3.0703-3.0664 7.2305-4.8008 11.57-4.8203 4.3398-0.015625 8.5117 1.6797 11.609 4.7227 3.0938 3.043 4.8633 7.1875 4.918 11.527 0 4.4219-1.7578 8.6602-4.8828 11.785s-7.3633 4.8828-11.785 4.8828zm2.918 13.707v8.375c0 1.6094-1.3086 2.918-2.918 2.918s-2.918-1.3086-2.918-2.918v-8.582c0-1.6133 1.3086-2.918 2.918-2.918s2.918 1.3047 2.918 2.918zm-22.168-11.125c1.1445 1.1523 1.1445 3.0117 0 4.168l-6.082 6.0391c-0.55078 0.5625-1.3008 0.875-2.0859 0.875-0.76953 0-1.5078-0.31641-2.0391-0.875-0.57031-0.54297-0.89453-1.293-0.89453-2.082s0.32422-1.5391 0.89453-2.082l6.082-6.125c1.1719-1.0938 3-1.0586 4.125 0.082031zm-10.918-16.332h-8.582c-1.6094 0-2.918-1.3086-2.918-2.918s1.3086-2.918 2.918-2.918h8.582c1.6133 0 2.918 1.3086 2.918 2.918s-1.3047 2.918-2.918 2.918zm0.70703-28.25h0.003907c-1.1523-1.1523-1.1523-3.0156 0-4.168 1.1484-1.1523 3.0156-1.1523 4.1641 0l6.043 6.125c1.1445 1.1523 1.1445 3.0117 0 4.168-0.56641 0.52734-1.3086 0.82422-2.082 0.83203-0.76562-0.003906-1.4961-0.30469-2.043-0.83203zm26.543-4.8359v-8.582c0-1.6094 1.3086-2.918 2.918-2.918s2.918 1.3086 2.918 2.918v8.582c0 1.6133-1.3086 2.918-2.918 2.918s-2.918-1.3047-2.918-2.918zm22.168 10.918c-1.1445-1.1523-1.1445-3.0117 0-4.168l6.082-6.082c1.1523-1.1523 3.0156-1.1523 4.168 0s1.1523 3.0156 0 4.168l-6.125 6.082c-0.54688 0.53125-1.2773 0.82812-2.043 0.83203-0.77344-0.003906-1.5156-0.30078-2.082-0.83203zm22.418 19.25c0 1.6094-1.3086 2.918-2.918 2.918h-8.582c-1.6133 0-2.918-1.3086-2.918-2.918s1.3047-2.918 2.918-2.918h8.582c0.77344 0 1.5156 0.30859 2.0625 0.85547s0.85547 1.2891 0.85547 2.0625zm-12.207 25.332h-0.003907c1.1445 1.1562 1.1445 3.0156 0 4.168-0.52734 0.5625-1.2695 0.87891-2.0391 0.875-0.78516 0-1.5352-0.31641-2.0859-0.875l-6.082-6.082c-1.1523-1.1523-1.1523-3.0156 0-4.168s3.0156-1.1523 4.168 0z"/>
                </svg>
            </button>
        </a>

        <a href="user/read">
            <button>
                <svg class="icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 -960 960 960" fill="#1f1f1f">
                    <path d="M95-203v-95h771v95H95Zm0-230v-94h771v94H95Zm0-229v-95h771v95H95Z"/>
                </svg>
            </button>
        </a>

        <a href="/logout">
            <button>
                <svg class="icon" xmlns="http://www.w3.org/2000/svg" height="48px" viewBox="0 -960 960 960" width="48px" fill="#1f1f1f"><path d="M180-120q-24 0-42-18t-18-42v-600q0-24 18-42t42-18h299v60H180v600h299v60H180Zm486-185-43-43 102-102H360v-60h363L621-612l43-43 176 176-174 174Z"/></svg>
            </button>
        </a>

    </div>
</nav>

<main>
    <section class="progress-wheel-section">
        <div id="container"></div>
    </section>

    <section class="scheudle-section">
        <section class="info-box">
            <div class="title" id="completion-estimation">
                Calculating completion time...
            </div>
            <div class="subtitle" id="already-completed-info">
                Starting with 50 questions already completed
            </div>
            <div class="subtitle" id="weekly-total-info">
                Calculating weekly total...
            </div>
        </section>

        <section class="schedule-calendar-section">
            <!-- Weekly Schedule Container -->
            <aside class="week-container">
                <h1>Weekly Schedule</h1>

                <div class="grid" id="day-headers">
                    <!-- Day headers will be added here -->
                </div>

                <div class="grid" id="target-inputs">
                    <!-- Target inputs will be added here -->
                </div>

                <div class="grid" id="completion-inputs">
                    <!-- Completion inputs will be added here -->
                </div>

                <div class="stats">
                    <div id="target-total">Target: 0</div>
                    <div id="done-total">Done: 0</div>
                    <div id="percentage">0%</div>
                </div>

                <div class="progress-bar">
                    <div class="progress-fill" id="progress-fill" style="width: 0%"></div>
                </div>

                <button id="update-chart">Update Schedule</button>
            </aside>

            <section class="schedule-chart-container">
                <h1>Question Completion Projection</h1>

                <div class="chart-container">
                    <canvas id="completionChart"></canvas>
                </div>
            </section>
        </section>
    </section>

    <section class="topic-section">
        <h1>Programming Topics Ranking</h1>

        <div class="instruction-box">
            <h2 style="margin-top: 0; font-size: 18px;">Drag and Drop to Reorder</h2>
            <p style="margin-bottom: 8px; font-size: 14px;">Drag any topic and drop it in a new position to reorder. The ranks will automatically update.</p>
            <div id="feedback" class="feedback">Topics reordered successfully!</div>
        </div>

        <ul id="topicList" class="topic-list">
            <!-- Topics will be inserted here by JavaScript -->
        </ul>
        <button id="save-changes">Save changes</button>
        <section class="topic-navigation-section">
            <button id="show-topics">Show more topics</button>
            <button id="hide-topics">Hide topics</button>
            <button id="show-all-topics">Show all topics</button>
        </section>

    </section>
</main>

</body>
</html>