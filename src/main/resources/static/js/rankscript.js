function createTopicList() {

    // Track state
    let topics = [];
    let nextHiddenItem = 6;
    let pendingChanges = false;
    // let updateTimer = null;
    // Track the active timeout
    let feedbackTimeout = null;

    $.ajax({
        url: "api/challenges/topics",
        method: "GET",
        data: {},
        dataType: "json",
        xhrFields: {
            withCredentials: true
        },
        success: function (data) {
            topics = data;
            renderTopics();
            setupEventListeners();
        }
    });

    // Render the topics list
    function renderTopics() {

        let itemCount = 1;

        const topicList = document.getElementById('topicList');
        topicList.innerHTML = '';

        topics.forEach((topic, index) => {
            // Create list item
            const li = document.createElement('li');
            li.className = 'topic-item';
            li.setAttribute('draggable', true);
            li.setAttribute('data-index', index);
            li.setAttribute('id', `topic-item-${index + 1}`);

            if (itemCount >= nextHiddenItem) {
                li.style.display = "none";
            }

            // Add drag handle icon
            const dragHandleIcon = `
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="8" y1="6" x2="21" y2="6"></line>
            <line x1="8" y1="12" x2="21" y2="12"></line>
            <line x1="8" y1="18" x2="21" y2="18"></line>
            <line x1="3" y1="6" x2="3.01" y2="6"></line>
            <line x1="3" y1="12" x2="3.01" y2="12"></line>
            <line x1="3" y1="18" x2="3.01" y2="18"></line>
        </svg>
        `;

            // Set content
            li.innerHTML = `
        <div class="topic-name">
            <span class="drag-handle">${dragHandleIcon}</span>
            ${topic.topicName}
        </div>
        <span class="rank-badge">rank: ${topic.topicRank}</span>
        `;

            topicList.appendChild(li);
            itemCount++;
        });

        // Show/hide save button based on pending changes
        const saveButton = document.getElementById('save-changes');
        if (saveButton) {
            saveButton.style.display = pendingChanges ? 'block' : 'none';
        }

        // Re-attach only drag event listeners after re-rendering
        setupDragListeners();

    }

    // Set up drag event listeners only
    function setupDragListeners() {
        // Get all draggable items
        const draggables = document.querySelectorAll('.topic-item');
        // These variables need to be in a scope accessible by all drag events
        let draggedElement = null;
        let dragStartIndex = null;

        // Add fresh listeners to all items
        draggables.forEach(item => {
            item.addEventListener('dragstart', function(e) {
                draggedElement = this;
                dragStartIndex = parseInt(this.getAttribute('data-index'));
                setTimeout(() => this.classList.add('dragging'), 0);
            });

            item.addEventListener('dragend', function() {
                this.classList.remove('dragging');
                document.querySelectorAll('.topic-item').forEach(item =>
                    item.classList.remove('drag-over')
                );
            });

            item.addEventListener('dragover', function(e) {
                e.preventDefault();
            });

            item.addEventListener('dragenter', function() {
                this.classList.add('drag-over');
            });

            item.addEventListener('dragleave', function() {
                this.classList.remove('drag-over');
            });

            item.addEventListener('drop', function(e) {
                e.preventDefault();
                const dropIndex = parseInt(this.getAttribute('data-index'));

                // Only do something if we're dropping to a different position
                if (dragStartIndex !== dropIndex && !isNaN(dragStartIndex) && !isNaN(dropIndex)) {
                    // Copy our topics array
                    const topicsCopy = [...topics];

                    // Get item we're dragging
                    const itemDragged = topicsCopy[dragStartIndex];

                    // Remove item from original array
                    topicsCopy.splice(dragStartIndex, 1);

                    // Add item to new array position
                    topicsCopy.splice(dropIndex, 0, itemDragged);

                    // Update ranks
                    topicsCopy.forEach((topic, index) => {
                        topic.topicRank = index + 1;
                    });

                    // Update our state
                    topics = topicsCopy;

                    // Set pending changes flag
                    pendingChanges = true;

                    // Re-render
                    renderTopics();

                    // Show feedback
                    showFeedback("Order changed - click Save to update");

                    // Set up debounced server update
                    // if (updateTimer) {
                    //     clearTimeout(updateTimer);
                    // }

                    // console.log(topics)

                    // Optional: Auto-save after delay
                    // updateTimer = setTimeout(saveChanges, 2000);
                }

                return false;
            });
        });
    }


    // Set up event listeners
    function setupEventListeners() {
        // Button event listeners
        const showMoreTopicsButton = document.getElementById('show-topics');
        if (showMoreTopicsButton) {
            showMoreTopicsButton.addEventListener('click', function() {
                let startCount = nextHiddenItem;
                for (let i = startCount; i <= startCount + 5; i++) {
                    console.log(i);
                    console.log("startcount  -->" + startCount);
                    if (i <= topics.length) {
                        const topic = document.getElementById(`topic-item-${i}`);
                        if (topic) topic.style.display = "";
                    } else {
                        break;
                    }
                }
                nextHiddenItem = Math.min(startCount + 6, topics.length + 1);
            });
        }

        const hideTopicsButton = document.getElementById('hide-topics');
        if (hideTopicsButton) {
            hideTopicsButton.addEventListener('click', function() {
                for (let i = 6; i <= topics.length; i++) {
                    const topic = document.getElementById(`topic-item-${i}`);
                    if (topic) topic.style.display = "none";
                }
                nextHiddenItem = 6;
            });
        }

        const showAllTopicsButton = document.getElementById('show-all-topics');
        if (showAllTopicsButton) {
            showAllTopicsButton.addEventListener('click', function() {
                for (let i = 1; i <= topics.length; i++) {
                    const topic = document.getElementById(`topic-item-${i}`);
                    if (topic) topic.style.display = "";
                }
                nextHiddenItem = topics.length + 1;
            });
        }

        // Add save button if it doesn't exist
        let saveButton = document.getElementById('save-changes');
        if (!saveButton) {
            saveButton = document.createElement('button');
            saveButton.id = 'save-changes';
            saveButton.className = 'btn btn-primary';
            saveButton.textContent = 'Save Changes';
            saveButton.style.display = 'none';

            // Add it to the page - adjust this to your layout
            const container = document.getElementById('topicList').parentElement;
            container.appendChild(saveButton);
        }

        saveButton.addEventListener('click', saveChanges);
    }

    // Save changes to the server
    function saveChanges() {
        if (!pendingChanges) return;

        // Show loading state
        showFeedback("Saving changes...", false);

        $.ajax({
            url: "api/challenges/update-topic-priority",
            method: "POST",
            data: JSON.stringify(topics),
            contentType: "application/json",
            xhrFields: {
                withCredentials: true
            },
            success: function() {
                pendingChanges = false;
                console.log("Changes saved successfully!");
                showFeedback("Changes saved successfully!");
                renderTopics(); // Update the UI to hide save button
            },
            error: function(xhr, status, error) {
                console.error("Error details:", xhr.responseText);
                console.log("Full XHR object:", xhr);
                console.log("Status:", status);
                console.log("Error:", error);
                console.log("Status code:", xhr.status);
                showFeedback("Error saving changes. Please try again.", true);
            }
        });
    }

    // Show feedback message
    function showFeedback(message, autoHide = true) {
        const feedback = document.getElementById('feedback');
        if (feedback) {
            // Clear any existing timeout
            if (feedbackTimeout) {
                clearTimeout(feedbackTimeout);
                feedbackTimeout = null;
            }

            feedback.textContent = message;
            feedback.style.display = 'block';

            if (autoHide) {
                feedbackTimeout = setTimeout(() => {
                    feedback.style.display = 'none';
                    feedbackTimeout = null;
                }, 4000);
            }
        }
    }

}



// const topics = [
//     { name: 'Array / String', rank: 1 },
//     { name: 'Two Pointers', rank: 2 },
//     { name: 'Sliding Window', rank: 3 },
//     { name: 'Matrix', rank: 4 },
//     { name: 'Hashmap', rank: 5 },
//     { name: 'Intervals', rank: 6 },
//     { name: 'Stack', rank: 7 },
//     { name: 'Linked List', rank: 8 },
//     { name: 'Binary Tree General', rank: 9 },
//     { name: 'Binary Tree BFS', rank: 10 },
//     { name: 'Binary Search Tree', rank: 11 },
//     { name: 'Graph General', rank: 12 },
//     { name: 'Graph BFS', rank: 13 },
//     { name: 'Trie', rank: 14 },
//     { name: 'Backtracking', rank: 15 },
//     { name: 'Divide & Conquer', rank: 16 },
//     { name: "Kadane's Algorithm", rank: 17 },
//     { name: 'Binary Search', rank: 18 },
//     { name: 'Heap', rank: 19 },
//     { name: 'Bit Manipulation', rank: 20 },
//     { name: 'Math', rank: 21 },
//     { name: '1D DP', rank: 22 },
//     { name: 'Multidimensional DP', rank: 23 }
// ];