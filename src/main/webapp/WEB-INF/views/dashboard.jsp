<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
    <head>

        <script>
            document.documentElement.classList.add('loading');
        </script>

        <style>
            html.loading {
                visibility: hidden;
            }
        </style>

        <script src="/js/theme.js"></script>

        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <link href="https://cdn.jsdelivr.net/npm/tom-select@2.4.3/dist/css/tom-select.bootstrap5.min.css" rel="stylesheet">
        <!-- Include D3.js (required dependency) -->
        <script src="https://d3js.org/d3.v7.min.js"></script>

        <!-- Include Cal-HeatMap JS and CSS -->
        <script src="https://unpkg.com/cal-heatmap/dist/cal-heatmap.min.js"></script>
        <script src="https://unpkg.com/cal-heatmap/dist/plugins/Legend.min.js"></script>
        <link rel="stylesheet" href="https://unpkg.com/cal-heatmap/dist/cal-heatmap.css">


        <script src="https://cdn.jsdelivr.net/npm/dompurify@3.0.3/dist/purify.min.js"></script>

        <script src="https://cdn.jsdelivr.net/npm/tom-select@2.4.3/dist/js/tom-select.complete.min.js"></script>


        <link rel="icon" type="image/svg+xml" href="/assets/favicon/favicon.svg">

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

        <script src="<c:url value='tinymce/js/tinymce/tinymce.min.js'/>"></script>

        <link rel="stylesheet" href="/css/dashboard.css">

        <title>HackerTracker</title>
    </head>

    <body>
        <nav id="main-nav">
            <img class="logo" src="/assets/hackertracker.svg" alt="HackerTracker Logo">

            <div class="navigation-buttons">

                <button id="lightSwitch">
                    <svg class="icon" xmlns="http://www.w3.org/2000/svg" version="1.1" viewBox="5.0 1.0 90.0 90.0" preserveAspectRatio="xMidYMid meet">
                        <path d="m50 27.707c-5.9141 0-11.582 2.3516-15.762 6.5312-4.1797 4.1797-6.5312 9.8477-6.5312 15.762s2.3516 11.582 6.5312 15.762c4.1797 4.1797 9.8477 6.5312 15.762 6.5312s11.582-2.3516 15.762-6.5312c4.1797-4.1797 6.5312-9.8477 6.5312-15.762-0.011719-5.9102-2.3633-11.574-6.543-15.75-4.1758-4.1797-9.8398-6.5312-15.75-6.543zm0 38.961c-4.3398-0.054688-8.4844-1.8242-11.527-4.918-3.043-3.0977-4.7383-7.2695-4.7227-11.609 0.019531-4.3398 1.7539-8.5 4.8203-11.57 3.0703-3.0664 7.2305-4.8008 11.57-4.8203 4.3398-0.015625 8.5117 1.6797 11.609 4.7227 3.0938 3.043 4.8633 7.1875 4.918 11.527 0 4.4219-1.7578 8.6602-4.8828 11.785s-7.3633 4.8828-11.785 4.8828zm2.918 13.707v8.375c0 1.6094-1.3086 2.918-2.918 2.918s-2.918-1.3086-2.918-2.918v-8.582c0-1.6133 1.3086-2.918 2.918-2.918s2.918 1.3047 2.918 2.918zm-22.168-11.125c1.1445 1.1523 1.1445 3.0117 0 4.168l-6.082 6.0391c-0.55078 0.5625-1.3008 0.875-2.0859 0.875-0.76953 0-1.5078-0.31641-2.0391-0.875-0.57031-0.54297-0.89453-1.293-0.89453-2.082s0.32422-1.5391 0.89453-2.082l6.082-6.125c1.1719-1.0938 3-1.0586 4.125 0.082031zm-10.918-16.332h-8.582c-1.6094 0-2.918-1.3086-2.918-2.918s1.3086-2.918 2.918-2.918h8.582c1.6133 0 2.918 1.3086 2.918 2.918s-1.3047 2.918-2.918 2.918zm0.70703-28.25h0.003907c-1.1523-1.1523-1.1523-3.0156 0-4.168 1.1484-1.1523 3.0156-1.1523 4.1641 0l6.043 6.125c1.1445 1.1523 1.1445 3.0117 0 4.168-0.56641 0.52734-1.3086 0.82422-2.082 0.83203-0.76562-0.003906-1.4961-0.30469-2.043-0.83203zm26.543-4.8359v-8.582c0-1.6094 1.3086-2.918 2.918-2.918s2.918 1.3086 2.918 2.918v8.582c0 1.6133-1.3086 2.918-2.918 2.918s-2.918-1.3047-2.918-2.918zm22.168 10.918c-1.1445-1.1523-1.1445-3.0117 0-4.168l6.082-6.082c1.1523-1.1523 3.0156-1.1523 4.168 0s1.1523 3.0156 0 4.168l-6.125 6.082c-0.54688 0.53125-1.2773 0.82812-2.043 0.83203-0.77344-0.003906-1.5156-0.30078-2.082-0.83203zm22.418 19.25c0 1.6094-1.3086 2.918-2.918 2.918h-8.582c-1.6133 0-2.918-1.3086-2.918-2.918s1.3047-2.918 2.918-2.918h8.582c0.77344 0 1.5156 0.30859 2.0625 0.85547s0.85547 1.2891 0.85547 2.0625zm-12.207 25.332h-0.003907c1.1445 1.1562 1.1445 3.0156 0 4.168-0.52734 0.5625-1.2695 0.87891-2.0391 0.875-0.78516 0-1.5352-0.31641-2.0859-0.875l-6.082-6.082c-1.1523-1.1523-1.1523-3.0156 0-4.168s3.0156-1.1523 4.168 0z"/>
                    </svg>
                </button>

                <a href="/progress">
                    <button>
                        <svg class="icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 -960 960 960" fill="#1f1f1f" preserveAspectRatio="xMidYMid meet">
                            <path d="M480-55q-90.2 0-167.57-32.58-77.37-32.57-134.82-90.03-57.46-57.45-90.03-134.82Q55-389.8 55-480q0-90.14 32.56-167.38 32.57-77.24 89.87-134.98 57.31-57.74 134.79-90.69Q389.7-906 480-906q19 0 33 14.59t14 33.5Q527-839 513-825q-14 14-33 14-138.01 0-234.51 96.49Q149-618.02 149-480.01t96.49 234.51q96.49 96.5 234.5 96.5t234.51-96.49Q811-341.99 811-480q0-19 14-33t32.91-14q18.91 0 33.5 14T909-480q0 90.2-32.96 167.68-32.95 77.49-90.46 134.84-57.51 57.34-134.78 89.91Q574.53-55 480-55Z"/>
                        </svg>
                    </button>
                </a>

                <a href="/user/read">
                    <button>
                        <svg class="icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 -960 960 960" fill="#1f1f1f" preserveAspectRatio="xMidYMid meet">
                            <path d="M95-203v-95h771v95H95Zm0-230v-94h771v94H95Zm0-229v-95h771v95H95Z"/>
                        </svg>
                    </button>
                </a>

                <a href="/logout">
                    <button>
                        <svg class="icon" xmlns="http://www.w3.org/2000/svg" height="48px" viewBox="0 -960 960 960" width="48px" fill="#1f1f1f" preserveAspectRatio="xMidYMid meet"><path d="M180-120q-24 0-42-18t-18-42v-600q0-24 18-42t42-18h299v60H180v600h299v60H180Zm486-185-43-43 102-102H360v-60h363L621-612l43-43 176 176-174 174Z"/></svg>
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

            <section id="overlay">
                <section id="search-section">
                    <div class="s010" id="s010">
                        <div class="form-container">
                            <div class="inner-form">
                                <div class="basic-search">
                                    <div class="input-field">
                                        <input id="search" type="text" placeholder="Enter Problem Title" />
                                        <div class="icon-wrap">
                                            <svg class="searchQueryBtn" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                                                <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"></path>
                                            </svg>
                                        </div>
                                    </div>
                                </div>
                                <div class="advance-search" id="advance-search">
                                    <span class="desc">ADVANCED SEARCH</span>
                                    <div class="row">
                                        <select id="select-topics"  multiple data-placeholder="Topics" class="tom-select-input">
<%--                                            <optgroup label="Topics">--%>
<%--                                                <option value="Array / String" selected>Array / String</option>--%>
<%--                                                <option value="Two Pointers">Two Pointers</option>--%>
<%--                                                <option value="Sliding Window">Sliding Window</option>--%>
<%--                                                <option value="Matrix">Matrix</option>--%>
<%--                                                <option value="Hashmap">Hashmap</option>--%>
<%--                                                <option value="Intervals">Intervals</option>--%>
<%--                                                <option value="Stack">Stack</option>--%>
<%--                                                <option value="Linked List">Linked List</option>--%>
<%--                                                <option value="Binary Tree General">Binary Tree General</option>--%>
<%--                                                <option value="Binary Tree BFS">Binary Tree BFS</option>--%>
<%--                                                <option value="Binary Search Tree">Binary Search Tree</option>--%>
<%--                                                <option value="Graph General">Graph General</option>--%>
<%--                                                <option value="Graph BFS">Graph BFS</option>--%>
<%--                                                <option value="Trie">Trie</option>--%>
<%--                                                <option value="Backtracking">Backtracking</option>--%>
<%--                                                <option value="Divide & Conquer">Divide & Conquer</option>--%>
<%--                                                <option value="Kadane's Algorithm">Kadane's Algorithm</option>--%>
<%--                                                <option value="Binary Search">Binary Search</option>--%>
<%--                                                <option value="Heap">Heap</option>--%>
<%--                                                <option value="Bit Manipulation">Bit Manipulation</option>--%>
<%--                                                <option value="Math">Math</option>--%>
<%--                                                <option value="1D DP">1D DP</option>--%>
<%--                                                <option value="Multidimensional DP">Multidimensional DP</option>--%>
<%--                                            </optgroup>--%>
                                        </select>

                                        <select id="select-tags"  multiple data-placeholder="Tags" class="tom-select-input">
<%--                                            <optgroup label="Tags">--%>
<%--                                                <option placeholder="" value="">Tags</option>--%>
<%--                                                <option value="Array" selected>Array</option>--%>
<%--                                                <option value="Sorting">Sorting</option>--%>
<%--                                                <option value="Two Pointers">Two Pointers</option>--%>
<%--                                                <option value="Counting">Counting</option>--%>
<%--                                                <option value="Divide and Conquer">Divide and Conquer</option>--%>
<%--                                                <option value="Hash Table">Hash Table</option>--%>
<%--                                                <option value="Math">Math</option>--%>
<%--                                                <option value="Dynamic Programming">Dynamic Programming</option>--%>
<%--                                                <option value="Greedy">Greedy</option>--%>
<%--                                                <option value="Counting Sort">Counting Sort</option>--%>
<%--                                                <option value="Design">Design</option>--%>
<%--                                                <option value="Randomized">Randomized</option>--%>
<%--                                                <option value="Prefix Sum">Prefix Sum</option>--%>
<%--                                                <option value="Monotonic Stack">Monotonic Stack</option>--%>
<%--                                                <option value="Stack">Stack</option>--%>
<%--                                                <option value="String">String</option>--%>
<%--                                                <option value="Trie">Trie</option>--%>
<%--                                                <option value="String Matching">String Matching</option>--%>
<%--                                                <option value="Simulation">Simulation</option>--%>
<%--                                                <option value="Binary Search">Binary Search</option>--%>
<%--                                                <option value="Sliding Window">Sliding Window</option>--%>
<%--                                                <option value="Matrix">Matrix</option>--%>
<%--                                                <option value="Union Find">Union Find</option>--%>
<%--                                                <option value="Recursion">Recursion</option>--%>
<%--                                                <option value="Linked List">Linked List</option>--%>
<%--                                                <option value="Doubly-Linked List">Doubly-Linked List</option>--%>
<%--                                                <option value="Binary Tree">Binary Tree</option>--%>
<%--                                                <option value="Breadth-First Search">Breadth-First Search</option>--%>
<%--                                                <option value="Depth-First Search">Depth-First Search</option>--%>
<%--                                                <option value="Tree">Tree</option>--%>
<%--                                                <option value="Binary Search Tree">Binary Search Tree</option>--%>
<%--                                                <option value="Iterator">Iterator</option>--%>
<%--                                                <option value="Bit Manipulation">Bit Manipulation</option>--%>
<%--                                                <option value="Graph">Graph</option>--%>
<%--                                                <option value="Shortest Path">Shortest Path</option>--%>
<%--                                                <option value="Topological Sort">Topological Sort</option>--%>
<%--                                                <option value="Backtracking">Backtracking</option>--%>
<%--                                                <option value="Merge Sort">Merge Sort</option>--%>
<%--                                                <option value="Heap (Priority Queue)">Heap (Priority Queue)</option>--%>
<%--                                                <option value="Monotonic Queue">Monotonic Queue</option>--%>
<%--                                                <option value="Queue">Queue</option>--%>
<%--                                                <option value="Quickselect">Quickselect</option>--%>
<%--                                                <option value="Data Stream">Data Stream</option>--%>
<%--                                                <option value="Geometry">Geometry</option>--%>
<%--                                                <option value="Memoization">Memoization</option>--%>
                                            </optgroup>
                                        </select>

                                        <select id="select-level"  multiple data-placeholder="Difficulty Level" class="tom-select-input">
                                            <optgroup label="Difficulty Level">
                                                <option placeholder="" value="">Difficulty Level</option>
                                                <option value="Easy">Easy</option>
                                                <option value="Medium">Medium</option>
                                                <option value="Hard">Hard</option>
                                            </optgroup>
                                        </select>

                                        <select id="select-status"  multiple data-placeholder="Completion status" class="tom-select-input">
                                            <optgroup label="Completition status">
                                                <option placeholder="" value="">Completion status</option>
                                                <option value="Completed" selected>Completed</option>
                                                <option value="Not Completed" selected>Not completed</option>
                                            </optgroup>
                                        </select>
                                    </div>
                                    <div class="row third">
                                        <div class="input-field">
<%--                                            <div class="result-count">--%>
<%--                                                <span>108 </span>results</div>--%>
                                            <div class="group-btn">
                                                <button class="btn-delete" id="delete">RESET</button>
                                                <button type="button" id="searchQueryBtn" class="searchQueryBtn">SEARCH</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="search-results" id="search-results" data-current-page="1" aria-live="polite">
                                    <div id="result-list"></div>
                                    <nav id="pagination-container">
                                        <button type="button" class="pagination-button" id="prev-button" aria-label="Previous page" title="Previous page">
                                            &lt;
                                        </button>

                                        <div id="pagination-numbers"></div>

                                        <button type="button" class="pagination-button" id="next-button" aria-label="Next page" title="Next page">
                                            &gt;
                                        </button>
                                    </nav>
                                </div>
                            </div>
                        </div>
                    </div>
                </section>
            </section>


            <section>

                <section id="challenge-card">

                    <aside class="question-navigation">
                        <section class="sticky-menu">
                            <button id="previousQuestionBtn" type="button">
                                <svg id="backward-arrow" viewBox="0 0 40 24">
                                    <path d="M35,12 L5,12 M15,2 L5,12 L15,22"></path>
                                </svg>
                            </button>
                            <button id="nextQuestionBtn" type="button">
                                <svg id="forward-arrow" viewBox="0 0 40 24">
                                    <path d="M5,12 L35,12 M25,2 L35,12 L25,22"></path>
                                </svg>
                            </button>

                            <button id="searchBtn">
                                <svg id="searchIcon" xmlns="http://www.w3.org/2000/svg" viewBox="0 -960 900 900"><path d="M796-121 533-384q-30 26-69.96 40.5Q423.08-329 378-329q-108.16 0-183.08-75Q120-479 120-585t75-181q75-75 181.5-75t181 75Q632-691 632-584.85 632-542 618-502q-14 40-42 75l264 262-44 44ZM377-389q81.25 0 138.13-57.5Q572-504 572-585t-56.87-138.5Q458.25-781 377-781q-82.08 0-139.54 57.5Q180-666 180-585t57.46 138.5Q294.92-389 377-389Z"/></svg>
                            </button>

                            <a href="#challengeDifficultyRating">
                                <button id="clockBtn">
                                    <svg id="clockIcon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                                        <circle cx="12" cy="12" r="10"></circle>
                                        <polyline points="12 6 12 12 16 14"></polyline>
                                    </svg>
                                </button>
                            </a>

                            <a href="#timerDisplay">
                                <button id="textAreaBtn">
                                    <svg id="textIcon" xmlns="http://www.w3.org/2000/svg" viewBox="0 -960 900 800"><path d="M160-410v-60h300v60H160Zm0-165v-60h470v60H160Zm0-165v-60h470v60H160Zm360 580v-123l221-220q9-9 20-13t22-4q12 0 23 4.5t20 13.5l37 37q9 9 13 20t4 22q0 11-4.5 22.5T862.09-380L643-160H520Zm300-263-37-37 37 37ZM580-220h38l121-122-18-19-19-18-122 121v38Zm141-141-19-18 37 37-18-19Z"/></svg>
                                </button>
                            </a>

                            <button id="skipQuestionBtn" type="button">
                                <svg id="skipIcon" xmlns="http://www.w3.org/2000/svg" viewBox="0 -960 900 900">
                                    <path d="M680-240v-480h60v480h-60Zm-460 0v-480l346 240-346 240Zm60-240Zm0 125 181-125-181-125v250Z"/>
                                </svg>
                            </button>

                            <button id="addAttemptBtn">
                                <svg id="checkmark" viewBox="0 0 36 25"><path d="M3,14.1L12,23L33,2"></path></svg>
                            </button>
                        </section>
                    </aside>

                    <section id="challenge-card-section">
                        <section class="challenge-card-head">
                            <div>
                                <h3 id="challengeTitle">Two Sums</h3>
                                <div id="challengeTopic">Topic: Algorithms</div>
                            </div>
                            <div class="access-challenge">
                                <p id="challengeLevel">Easy</p>
                                <a id="challengeUrl" href="" target='_blank'>
                                    <button type="button">
                                        <svg class="icon" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="2 0 90 85" style="enable-background:new 0 0 100 100;" xml:space="preserve"><title>61 all</title><path d="M28.8,84.1h36l0,0c7.2,0,13-5.8,13-13v-22c0-1.7-1.3-3-3-3l0,0c-1.7,0-3,1.3-3,3v22c0,3.9-3.1,7-7,7l0,0h-36  c-3.9,0-7-3.1-7-7v-36c0-3.9,3.1-7,7-7l0,0h22l0,0c1.7,0,3-1.3,3-3s-1.3-3-3-3h-22l0,0c-7.2,0-13,5.8-13,13l0,0v36  C15.8,78.3,21.6,84.1,28.8,84.1z"/><path d="M84.2,37.2V18.9c0-0.2,0-0.4-0.1-0.6c0-0.1,0-0.2-0.1-0.2c0-0.1-0.1-0.2-0.1-0.3c0-0.1-0.1-0.2-0.2-0.3  c0-0.1-0.1-0.2-0.1-0.2c-0.2-0.3-0.5-0.6-0.8-0.8l-0.2-0.1C82.4,16.1,82.2,16,82,16l-0.3-0.1c-0.2,0-0.4-0.1-0.6-0.1l0,0H62.8l0,0  c-1.7,0-3,1.3-3,3s1.3,3,3,3h11.1L46.4,49.4c-1.2,1.2-1.2,3.1,0,4.2c1.2,1.2,3.1,1.2,4.2,0l27.6-27.5v11.1c0,1.7,1.3,3,3,3l0,0  C82.8,40.2,84.1,38.8,84.2,37.2z"/></svg>
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
                                            <svg class="clock-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
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
                            </section>

                        </section>
                    </section>

                </section>

            </section>

        </main>

        <script src="/js/searchscript.js"></script>

        <script src="/js/timer.js"></script>

        <script src="/js/heatmap.js"></script>

        <script src="/js/loadview.js" ></script>

    </body>

</html>