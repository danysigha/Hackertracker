
const topics = [
    { name: 'Array / String', rank: 1 },
    { name: 'Two Pointers', rank: 2 },
    { name: 'Sliding Window', rank: 3 },
    { name: 'Matrix', rank: 4 },
    { name: 'Hashmap', rank: 5 },
    { name: 'Intervals', rank: 6 },
    { name: 'Stack', rank: 7 },
    { name: 'Linked List', rank: 8 },
    { name: 'Binary Tree General', rank: 9 },
    { name: 'Binary Tree BFS', rank: 10 },
    { name: 'Binary Search Tree', rank: 11 },
    { name: 'Graph General', rank: 12 },
    { name: 'Graph BFS', rank: 13 },
    { name: 'Trie', rank: 14 },
    { name: 'Backtracking', rank: 15 },
    { name: 'Divide & Conquer', rank: 16 },
    { name: "Kadane's Algorithm", rank: 17 },
    { name: 'Binary Search', rank: 18 },
    { name: 'Heap', rank: 19 },
    { name: 'Bit Manipulation', rank: 20 },
    { name: 'Math', rank: 21 },
    { name: '1D DP', rank: 22 },
    { name: 'Multidimensional DP', rank: 23 }
];


// Track the dragged element and its index
let draggedElement = null;
let dragStartIndex = null;
let nextHiddenItem = 6;



// Add events to the topic list
function addEventListeners() {
    const draggables = document.querySelectorAll('.topic-item');
    const topicListContainer = document.getElementById('topicList');

    draggables.forEach(item => {
        item.addEventListener('dragstart', dragStart);
        item.addEventListener('dragend', dragEnd);
        item.addEventListener('dragover', dragOver);
        item.addEventListener('drop', drop);
        item.addEventListener('dragenter', dragEnter);
        item.addEventListener('dragleave', dragLeave);
    });

    const showMoreTopicsButton = document.getElementById('show-topics');
    showMoreTopicsButton.addEventListener('click', showMoreTopics);

    const hideTopicsButton = document.getElementById('hide-topics');
    hideTopicsButton.addEventListener('click', hideTopics);

    const showAllTopicsButton = document.getElementById('show-all-topics');
    showAllTopicsButton.addEventListener('click', showAllTopics);
}

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
        li.setAttribute('id', `topic-item-${itemCount}`);

        if(itemCount >= nextHiddenItem) {
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
            ${topic.name}
        </div>
        <span class="rank-badge">rank: ${topic.rank}</span>
        `;

        topicList.appendChild(li);
        itemCount++;
    });

    // Need to add event listeners after rendering
    addEventListeners();
}

// Drag event functions
function dragStart() {
    draggedElement = this;
    dragStartIndex = parseInt(this.getAttribute('data-index'));
    setTimeout(() => this.classList.add('dragging'), 0);
}

function dragEnd() {
    this.classList.remove('dragging');
    const items = document.querySelectorAll('.topic-item');
    items.forEach(item => item.classList.remove('drag-over'));
}

function dragOver(e) {
    e.preventDefault();
}

function dragEnter() {
    this.classList.add('drag-over');
}

function dragLeave() {
    this.classList.remove('drag-over');
}

function drop(e) {
    e.preventDefault();
    const dropIndex = parseInt(this.getAttribute('data-index'));

    // Only do something if we're dropping to a different position
    if (dragStartIndex !== dropIndex) {
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
            topic.rank = index + 1;
        });

        // Update our state
        topics.length = 0;
        topics.push(...topicsCopy);

        // Re-render
        renderTopics();

        // Show feedback
        showFeedback();
    }

    return false;
}

// Show success feedback
function showFeedback() {
    const feedback = document.getElementById('feedback');
    feedback.style.display = 'block';

    setTimeout(() => {
        feedback.style.display = 'none';
    }, 2000);
}

function showMoreTopics() {
    console.log("inside showMoreTopics");
    let startCount = nextHiddenItem;
    for(let i = startCount; i <= startCount + 5; i++) {
        if(nextHiddenItem <= topics.length) {
            const topic = document.getElementById(`topic-item-${i}`);
            topic.style.display = "";
            nextHiddenItem++;
        } else {
            break;
        }
    }
}

function showAllTopics() {
    console.log("inside showAllTopics");
    for(let i = nextHiddenItem; i <= topics.length; i++) {
        const topic = document.getElementById(`topic-item-${i}`);
        topic.style.display = "";
        nextHiddenItem++;
    }
}

function hideTopics() {
    console.log("inside hideTopics");
    console.log(nextHiddenItem);
    for(let i = 6; i < nextHiddenItem; i++) {
        const topic = document.getElementById(`topic-item-${i}`);
        topic.style.display = "none";
    }
    nextHiddenItem = 6;
}