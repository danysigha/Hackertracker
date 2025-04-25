<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

        <!-- Include D3.js (required dependency) -->
        <script src="https://d3js.org/d3.v7.min.js"></script>

        <!-- Include Cal-HeatMap JS and CSS -->
        <script src="https://unpkg.com/cal-heatmap/dist/plugins/Legend.min.js"></script>
        <script src="https://unpkg.com/cal-heatmap/dist/cal-heatmap.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/dompurify@3.0.3/dist/purify.min.js"></script>
        <link rel="stylesheet" href="https://unpkg.com/cal-heatmap/dist/cal-heatmap.css">

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

        <script src="<c:url value='tinymce/js/tinymce/tinymce.min.js'/>"></script>

        <script>
            tinymce.init({
                selector: '#myEditor',
                plugins: 'link lists code',
                toolbar: 'undo redo | formatselect | bold italic | alignleft aligncenter alignright | bullist numlist | link code',
                height: 400,
                // skin: 'oxide-dark',
                // content_css: 'dark'
            });
        </script>

        <link rel="stylesheet" href="/css/baseStyle.css">
        <link rel="stylesheet" href="/css/homeStyle.css">
        <title>HackerTracker</title>
    </head>

    <body>
        <nav>
            <img class="logo" src="/assets/hackertracker.png" alt="HackerTracker Logo">

            <div class="navigation-buttons">

                <a href="">
                    <button>
                        <svg class="icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 -960 960 960" fill="#1f1f1f">
                            <path d="M480-55q-90.2 0-167.57-32.58-77.37-32.57-134.82-90.03-57.46-57.45-90.03-134.82Q55-389.8 55-480q0-90.14 32.56-167.38 32.57-77.24 89.87-134.98 57.31-57.74 134.79-90.69Q389.7-906 480-906q19 0 33 14.59t14 33.5Q527-839 513-825q-14 14-33 14-138.01 0-234.51 96.49Q149-618.02 149-480.01t96.49 234.51q96.49 96.5 234.5 96.5t234.51-96.49Q811-341.99 811-480q0-19 14-33t32.91-14q18.91 0 33.5 14T909-480q0 90.2-32.96 167.68-32.95 77.49-90.46 134.84-57.51 57.34-134.78 89.91Q574.53-55 480-55Z"/>
                        </svg>
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

            </div>
        </nav>

        <main>
            <section>
                <div id="cal-heatmap"></div>
                <div>
                    <button id="navigation-backward">← Previous</button>

                    <button id="navigation-forward">Next →</button>

                    <div id="legend-label"></div>
                </div>
            </section>

            <section>

                <section id="challenge-card">

                    <section class="challenge-card-head">
                        <div>
                            <h3 id="challengeTitle">Two Sums</h3>
                            <div id="challengeTopic">Topic: Algorithms</div>
                        </div>
                        <div class="access-challenge">
                            <p id="challengeLevel">Easy</p>
                            <a id="challengeUrl" href="" target='_blank'>
                                <button type="button">
                                    <svg class="icon" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" version="1.1" x="0px" y="0px" viewBox="2 0 90 85" style="enable-background:new 0 0 100 100;" xml:space="preserve"><title>61 all</title><path d="M28.8,84.1h36l0,0c7.2,0,13-5.8,13-13v-22c0-1.7-1.3-3-3-3l0,0c-1.7,0-3,1.3-3,3v22c0,3.9-3.1,7-7,7l0,0h-36  c-3.9,0-7-3.1-7-7v-36c0-3.9,3.1-7,7-7l0,0h22l0,0c1.7,0,3-1.3,3-3s-1.3-3-3-3h-22l0,0c-7.2,0-13,5.8-13,13l0,0v36  C15.8,78.3,21.6,84.1,28.8,84.1z"/><path d="M84.2,37.2V18.9c0-0.2,0-0.4-0.1-0.6c0-0.1,0-0.2-0.1-0.2c0-0.1-0.1-0.2-0.1-0.3c0-0.1-0.1-0.2-0.2-0.3  c0-0.1-0.1-0.2-0.1-0.2c-0.2-0.3-0.5-0.6-0.8-0.8l-0.2-0.1C82.4,16.1,82.2,16,82,16l-0.3-0.1c-0.2,0-0.4-0.1-0.6-0.1l0,0H62.8l0,0  c-1.7,0-3,1.3-3,3s1.3,3,3,3h11.1L46.4,49.4c-1.2,1.2-1.2,3.1,0,4.2c1.2,1.2,3.1,1.2,4.2,0l27.6-27.5v11.1c0,1.7,1.3,3,3,3l0,0  C82.8,40.2,84.1,38.8,84.2,37.2z"/></svg>
                                </button>
                            </a>
                        </div>
                    </section>

                    <section>

                        <section class="tag-section">
                            <h4 class="tags-heading">Tags</h4>
                            <div id="challengeTags" class="tags-list">
                            </div>
                        </section>

                        <section class="difficulty-section">

                            <h4 id="challengeDifficultyRating" class="difficulty-heading">Difficulty Rating (3/10)</h4>

                            <div class="difficulty-container">
                                <input
                                        type="range"
                                        min="0"
                                        max="10"
                                        value="3"
                                        class="difficulty-slider"
                                        id="difficultySlider"
                                >
                                <div class="difficulty-ticks">
                                    <span>0</span>
                                    <span>1</span>
                                    <span>2</span>
                                    <span>3</span>
                                    <span>4</span>
                                    <span>5</span>
                                    <span>6</span>
                                    <span>7</span>
                                    <span>8</span>
                                    <span>9</span>
                                    <span>10</span>
                                </div>
                            </div>

                        </section>


                        <section class="timer-section">
                            <div>
                                <div class="timer-header">
                                    <h3 class="timer-title">
                                        <svg class="clock-icon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                            <circle cx="12" cy="12" r="10"></circle>
                                            <polyline points="12 6 12 12 16 14"></polyline>
                                        </svg>
                                        Timer
                                    </h3>
                                    <div class="timer-display" id="timerDisplay">00:00:00</div>
                                </div>

                                <div class="timer-info">
                                    <div class="time-item">
                                        <span class="time-label">Start Time</span>
                                        <span class="time-value" id="startTimeDisplay">--:--:--</span>
                                    </div>
                                    <div class="time-item">
                                        <span class="time-label">End Time</span>
                                        <span class="time-value" id="endTimeDisplay">--:--:--</span>
                                    </div>
                                </div>

                                <div class="timer-controls">
                                    <button type = "button" class="timer-button start-button" id="startTimerBtn">Start Timer</button>
                                    <button type = "button" class="timer-button stop-button" id="stopTimerBtn" style="display: none;">Stop Timer</button>
                                </div>
                            </div>

                        </section>

                        <section class="notes-section">
                            <h4 class="notes-heading">Notes</h4>
                            <textarea id="myEditor" name="content"></textarea>
                            <br/>
                            <div>
                                <button id="previousQuestionBtn" type="button">← Back</button>
                                <button id="nextQuestionBtn" type="button">Next →</button>
                                <button id="skipQuestionBtn" type="button">Skip</button>
                                <input type="submit" value="Mark completed" id="addAttemptBtn">
                            </div>
                        </section>

                    </section>

                </section>

            </section>

        </main>

        <script type="module" src="/js/loadview.js" ></script>

        <script src="/js/timer.js"></script>

        <script src="/js/heatmap.js"></script>

    </body>

</html>