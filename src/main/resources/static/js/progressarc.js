import * as d3 from "https://cdn.jsdelivr.net/npm/d3@7/+esm";

function createVisualization() {

    d3.select("#container").selectAll("svg").remove();

    const container = d3.select("#container");

    const width = container.node().getBoundingClientRect().width;
    const height = container.node().getBoundingClientRect().height;

    // position for all arcs
    const centerX = window.innerWidth/2.7;
    const centerY = height/1.7;

    // Arc properties

    // Arc properties - scale based on the smaller of width or height
    // This ensures the visualization stays proportional in both dimensions
    const baseSize = Math.min(width, height);
    const innerRadius = baseSize * 0.50; // Make radius responsive to both width and height
    const outerRadius = innerRadius + (baseSize * 0.030); // Proportional thickness
    const padding = 0.05;

    // const innerRadius = 235;
    // const outerRadius = 250;
    // const padding = 0.05;

    // Define total arc range
    const totalArcStart = -Math.PI * 0.7;
    const totalArcEnd = Math.PI * 0.7;
    const totalArcSpan = totalArcEnd - totalArcStart;

    // Progress data
    const easyProgressData = {
        total: 38,
        completed: 12,
        color: "#A8DCAB", // Teal for Easy
        progressColor: "green"
    };

    const mediumProgressData = {
        total: 100,
        completed: 50,
        color: "#FFDBBB", // Amber for Medium
        progressColor: "#FFAB00"
    };

    const hardProgressData = {
        total: 12,
        completed: 6,
        color: "#FF7F7F", // Red for Hard
        progressColor: "#E53935"
    };

    // Calculate arc angles dynamically based on the proportion of problems
    const totalProblems = easyProgressData.total + mediumProgressData.total + hardProgressData.total;

    // Easy arc
    const easyStartAngle = totalArcStart;
    const easyRatio = easyProgressData.total / totalProblems;
    const easyEndAngle = easyStartAngle + (totalArcSpan * easyRatio);

    // Medium arc
    const mediumStartAngle = easyEndAngle;
    const mediumRatio = mediumProgressData.total / totalProblems;
    const mediumEndAngle = mediumStartAngle + (totalArcSpan * mediumRatio);

    // Hard arc
    const hardStartAngle = mediumEndAngle;
    const hardRatio = hardProgressData.total / totalProblems;
    const hardEndAngle = totalArcEnd; // Ensure it ends exactly at the end point

    // Totals
    const totalSolved = 83;
    const attempts = 27;

    // Create SVG directly within the container (no d3.create)
    const svg = container.append("svg")
        // .attr("width", width)
        // .attr("height", height)
        .attr("width", "100%")
        .attr("height", "100%")
        .attr("viewBox", `0 0 ${width} ${height}`)
        .attr("preserveAspectRatio", "xMidYMid meet");

    const easyArcGenerator = d3.arc()
        .innerRadius(innerRadius)
        .outerRadius(outerRadius)
        .cornerRadius(100)
        .padAngle(padding)
        .startAngle(easyStartAngle)
        .endAngle(easyEndAngle);

    const mediumArcGenerator = d3.arc()
        .innerRadius(innerRadius)
        .outerRadius(outerRadius)
        .cornerRadius(100)
        .padAngle(padding)
        .startAngle(mediumStartAngle)
        .endAngle(mediumEndAngle);

    const hardArcGenerator = d3.arc()
        .innerRadius(innerRadius)
        .outerRadius(outerRadius)
        .cornerRadius(100)
        .padAngle(padding)
        .startAngle(hardStartAngle)
        .endAngle(hardEndAngle);

    // Add all BACKGROUND arcs first
    svg.append("path")
        .attr("transform", `translate(${centerX},${centerY})`)
        .attr("d", easyArcGenerator())
        .attr("fill", easyProgressData.color);

    svg.append("path")
        .attr("transform", `translate(${centerX},${centerY})`)
        .attr("d", mediumArcGenerator())
        .attr("fill", mediumProgressData.color);

    svg.append("path")
        .attr("transform", `translate(${centerX},${centerY})`)
        .attr("d", hardArcGenerator())
        .attr("fill", hardProgressData.color);


    // Calculate progress end angles
    const easyProgressAngle = easyStartAngle + (easyEndAngle - easyStartAngle) * (easyProgressData.completed / easyProgressData.total);
    const mediumProgressAngle = mediumStartAngle + (mediumEndAngle - mediumStartAngle) * (mediumProgressData.completed / mediumProgressData.total);
    const hardProgressAngle = hardStartAngle + (hardEndAngle - hardStartAngle) * (hardProgressData.completed / hardProgressData.total);

    // FORWARD ARCS

    // Progress arcs with animation
    const easyProgressArc = svg.append("path")
        .attr("transform", `translate(${centerX},${centerY})`)
        .attr("d", easyArcGenerator.endAngle(easyStartAngle)())
        .attr("fill", easyProgressData.progressColor);

    easyProgressArc.transition()
        .duration(1500)
        .delay(300)
        .attrTween("d", function() {
            const interpolate = d3.interpolate(easyStartAngle, easyProgressAngle);
            return function(t) {
                return easyArcGenerator.endAngle(interpolate(t))();
            };
        });

    const mediumProgressArc = svg.append("path")
        .attr("transform", `translate(${centerX},${centerY})`)
        .attr("d", mediumArcGenerator.endAngle(mediumStartAngle)())
        .attr("fill", mediumProgressData.progressColor);

    mediumProgressArc.transition()
        .duration(1800)
        .delay(600)
        .attrTween("d", function() {
            const interpolate = d3.interpolate(mediumStartAngle, mediumProgressAngle);
            return function(t) {
                return mediumArcGenerator.endAngle(interpolate(t))();
            };
        });

    const hardProgressArc = svg.append("path")
        .attr("transform", `translate(${centerX},${centerY})`)
        .attr("d", hardArcGenerator.endAngle(hardStartAngle)())
        .attr("fill", hardProgressData.progressColor);

    hardProgressArc.transition()
        .duration(1500)
        .delay(900)
        .attrTween("d", function() {
            const interpolate = d3.interpolate(hardStartAngle, hardProgressAngle);
            return function(t) {
                return hardArcGenerator.endAngle(interpolate(t))();
            };
        });

    // Add completed number in center
    const progressNumberGroup = svg.append("g")
        .attr("transform", `translate(${centerX + baseSize * 0.02},${centerY - baseSize * 0.25})`);

    const progressNumber = progressNumberGroup.append("text")
        .attr("class", "center-label--completed")
        .attr("text-anchor", "end")
        .attr("dominant-baseline", "central")
        .attr("x", 0)
        .attr("style", `font-size: ${baseSize * 0.25}px`)
        .text("0");

// Add total number
    progressNumberGroup.append("text")
        .attr("class", "center-label-missing")
        .attr("text-anchor", "start")
        .attr("dominant-baseline", "central")
        .attr("x", 10)
        .attr("y", 0)
        .attr("style", `font-size: ${baseSize * 0.1}px`)
        .text(`/${totalProblems}`);

// "Solved" label
    svg.append("text")
        .attr("class", "center-solved-label")
        .attr("text-anchor", "middle")
        .attr("dominant-baseline", "central")
        .attr("x", centerX + baseSize * 0.1)
        .attr("y", centerY + baseSize * 0.01)
        .attr("style", `font-size: ${baseSize * 0.1}px`)
        .text("Solved")
        .style("opacity", 0)
        .transition()
        .duration(500)
        .delay(1800)
        .style("opacity", 1);

// "Attempting" label
    svg.append("text")
        .attr("class", "bottom-attemps-label")
        .attr("text-anchor", "middle")
        .attr("dominant-baseline", "central")
        .attr("x", centerX)
        .attr("y", centerY + baseSize * 0.28)
        .attr("style", `font-size: ${baseSize * 0.09}px`)
        .text(`${attempts} Attempts`)
        .style("opacity", 0)
        .transition()
        .duration(500)
        .delay(2100)
        .style("opacity", 1);

// Animate progress number
    const startCount = 0;
    const duration = 2000;

    const timer = d3.timer(function(elapsed) {
        const progress = Math.min(1, elapsed / duration);
        const currentCount = Math.floor(progress * totalSolved);
        progressNumber.text(currentCount);

        if (progress === 1) timer.stop();
    });

// Add difficulty labels on the right side with boxes
    // const rightSideX = centerX + width * 0.40;
    // const startY = centerY - height * 0.35;
    // const spacing = height * 0.25;
    // const boxWidth = width * 0.15;
    // const boxHeight = height * 0.17;
    // const boxRadius = 8;

    // Add difficulty labels on the right side with boxes
    // Use relative positioning based on the base size for consistency
    const rightSideX = centerX + baseSize * 0.85;
    const startY = centerY - baseSize * 0.35;
    const spacing = baseSize * 0.25; // Consistent spacing based on the base size
    const boxWidth = baseSize * 0.3;
    const boxHeight = baseSize * 0.17;
    const boxRadius = Math.max(4, baseSize * 0.01); // Responsive border radius with minimum size

// Easy category box and labels
    svg.append("rect")
        .attr("x", rightSideX - boxWidth/2.1)
        .attr("y", startY - boxHeight/2.7)
        .attr("width", boxWidth)
        .attr("height", boxHeight)
        .attr("rx", boxRadius)
        .attr("ry", boxRadius)
        .attr("fill", "#333333") // Dark background for box
        .attr("opacity", 0.7);

// Easy label
    const easyGroup = svg.append("g")
        .attr("transform", `translate(${rightSideX}, ${startY})`);

    easyGroup.append("text")
        .attr("text-anchor", "middle")
        .attr("x", `${baseSize * 0.015}`)
        .attr("style", `font-size: ${baseSize * 0.05}px`)
        .attr("fill", easyProgressData.color)
        .text("Easy");

    const easyCountGroup = svg.append("g")
        .attr("transform", `translate(${rightSideX}, ${startY + baseSize * 0.04})`);

    const easyCount = easyCountGroup.append("text")
        .attr("class", "category-label")
        .attr("text-anchor", "end")
        .attr("x", -3)
        .attr("y", `${baseSize * 0.03}px`)
        .attr("style", `font-size: ${baseSize * 0.045}px`)
        .text("0");

    easyCountGroup.append("text")
        .attr("class", "category-label")
        .attr("text-anchor", "start")
        .attr("x", 2)
        .attr("y", `${baseSize * 0.03}px`)
        .attr("style", `font-size: ${baseSize * 0.045}px`)
        .text(`/${easyProgressData.total}`);


// Medium category box and labels
    svg.append("rect")
        .attr("x", rightSideX - boxWidth/2.1)
        .attr("y", startY + spacing - boxHeight/2.7)
        .attr("width", boxWidth)
        .attr("height", boxHeight)
        .attr("rx", boxRadius)
        .attr("ry", boxRadius)
        .attr("fill", "#333333") // Dark background for box
        .attr("opacity", 0.7);

// Medium label
    const mediumGroup = svg.append("g")
        .attr("transform", `translate(${rightSideX}, ${startY + spacing})`);

    mediumGroup.append("text")
        .attr("text-anchor", "middle")
        .attr("x", `${baseSize * 0.015}`)
        .attr("style", `font-size: ${baseSize * 0.05}px`)
        .attr("fill", mediumProgressData.color)
        .text("Med.");

    const mediumCountGroup = svg.append("g")
        .attr("transform", `translate(${rightSideX}, ${startY + spacing + baseSize * 0.04})`);

    const mediumCount = mediumCountGroup.append("text")
        .attr("class", "category-label")
        .attr("text-anchor", "end")
        .attr("x", -3)
        .attr("y", `${baseSize * 0.03}px`)
        .attr("style", `font-size: ${baseSize * 0.045}px`)
        .text("0");

    mediumCountGroup.append("text")
        .attr("class", "category-label")
        .attr("text-anchor", "start")
        .attr("x", 2)
        .attr("y", `${baseSize * 0.03}px`)
        .attr("style", `font-size: ${baseSize * 0.045}px`)
        .text(`/${mediumProgressData.total}`);


    svg.append("rect")
        .attr("x", rightSideX - boxWidth/2.1)
        .attr("y", startY + spacing * 2 - boxHeight/2.7)
        .attr("width", boxWidth)
        .attr("height", boxHeight)
        .attr("rx", boxRadius)
        .attr("ry", boxRadius)
        .attr("fill", "#333333") // Dark background for box
        .attr("opacity", 0.7);

// Hard label
    const hardGroup = svg.append("g")
        .attr("transform", `translate(${rightSideX}, ${startY + spacing * 2})`);

    hardGroup.append("text")
        .attr("text-anchor", "middle")
        .attr("x", `${baseSize * 0.015}`)
        .attr("style", `font-size: ${baseSize * 0.05}px`)
        .attr("fill", hardProgressData.color)
        .text("Hard");

    const hardCountGroup = svg.append("g")
        .attr("transform", `translate(${rightSideX}, ${startY + spacing * 2 + baseSize * 0.04})`);

    const hardCount = hardCountGroup.append("text")
        .attr("class", "category-label")
        .attr("text-anchor", "end")
        .attr("x", -3)
        .attr("y", `${baseSize * 0.03}px`)
        .attr("style", `font-size: ${baseSize * 0.045}px`)
        .text("0");

    hardCountGroup.append("text")
        .attr("class", "category-label")
        .attr("text-anchor", "start")
        .attr("x", 2)
        .attr("y", `${baseSize * 0.03}px`)
        .attr("style", `font-size: ${baseSize * 0.045}px`)
        .text(`/${hardProgressData.total}`);

// Animate count numbers for each difficulty
    d3.timer(function(elapsed) {
        const progress = Math.min(1, elapsed / duration);

        easyCount.text(Math.floor(progress * easyProgressData.completed));
        mediumCount.text(Math.floor(progress * mediumProgressData.completed));
        hardCount.text(Math.floor(progress * hardProgressData.completed));

        if (progress === 1) return true; // Stop timer
    });

// Add checkmark icon
    const checkmarkX = centerX - baseSize * 0.2;
    const checkmarkY = centerY - baseSize * 0.035;
    //const strokeWidth = baseSize * 0.01; // 1% of the smallest dimension


    svg.append("path")
        .attr("d", "M3,14.1L12,23L33,2")
        .attr("transform", `translate(${checkmarkX},${checkmarkY}) scale(${baseSize * 0.003})`)
        .attr("stroke", "#4CAF50")
        .attr("stroke-width", 5)
        .attr("fill", "none")
        .style("opacity", 0)
        .transition()
        .duration(500)
        .delay(1500)
        .style("opacity", 1);
}



function createPredictionVisualization() {
    // Default values
    let totalQuestions = 150;
    let alreadyCompleted = 50;
    let chart; // Reference to the chart object

    const daysOfWeek = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];

    // Initial state
    let questionsPerDay = {
        Mon: 1, Tue: 2, Wed: 0, Thu: 0, Fri: 3, Sat: 1, Sun: 0
    };

    let completedQuestions = {
        Mon: 0, Tue: 0, Wed: 0, Thu: 0, Fri: 0, Sat: 0, Sun: 0
    };

    // Setup the day headers
    const dayHeadersContainer = document.getElementById('day-headers');
    daysOfWeek.forEach(day => {
        const dayHeader = document.createElement('div');
        dayHeader.className = 'day-header';
        dayHeader.textContent = day;
        dayHeadersContainer.appendChild(dayHeader);
    });

    // Setup target inputs
    const targetInputsContainer = document.getElementById('target-inputs');
    daysOfWeek.forEach(day => {
        const inputContainer = document.createElement('div');
        inputContainer.className = 'input-container';

        const input = document.createElement('input');
        input.type = 'number';
        input.min = '0';
        input.value = questionsPerDay[day];
        input.id = `target-${day}`;
        input.setAttribute('aria-label', `Target questions for ${day}`);
        input.addEventListener('input', function() {
            handleTargetChange(day, this.value, questionsPerDay, completedQuestions);
        });

        inputContainer.appendChild(input);
        targetInputsContainer.appendChild(inputContainer);
    });

    // Setup completion inputs
    const completionInputsContainer = document.getElementById('completion-inputs');
    daysOfWeek.forEach(day => {
        const inputContainer = document.createElement('div');
        inputContainer.className = 'input-container';

        const input = document.createElement('input');
        input.type = 'number';
        input.min = '0';
        input.value = completedQuestions[day];
        input.className = 'completion-input';
        input.id = `completion-${day}`;
        input.setAttribute('aria-label', `Completed questions for ${day}`);
        input.addEventListener('input', function() {
            handleCompletedChange(day, this.value, questionsPerDay, completedQuestions);
        });

        inputContainer.appendChild(input);
        completionInputsContainer.appendChild(inputContainer);
    });

    // Initialize projection chart
    chart = createChart(totalQuestions, alreadyCompleted);

    // Update chart when button is clicked
    document.getElementById('update-chart').addEventListener('click', function() {
        // Get the current schedule from inputs
        daysOfWeek.forEach(day => {
            questionsPerDay[day] = parseInt(document.getElementById(`target-${day}`).value) || 0;
        });

        // Get total goal and already completed values
        totalQuestions = 150;
        alreadyCompleted = 50;

        // Update the chart
        if (chart) {
            chart.destroy(); // Destroy previous chart if exists
        }
        chart = createChart(totalQuestions, alreadyCompleted);
    });

    // Initialize stats
    updateStats(questionsPerDay, completedQuestions);
}

// Handle target change
function handleTargetChange(day, value, questionsPerDay, completedQuestions) {
    const numValue = Math.max(0, parseInt(value) || 0);
    questionsPerDay[day] = numValue;
    updateStats(questionsPerDay, completedQuestions);
}

// Handle completed change
function handleCompletedChange(day, value, questionsPerDay, completedQuestions) {
    const numValue = Math.max(0, parseInt(value) || 0);
    completedQuestions[day] = numValue;
    updateStats(questionsPerDay, completedQuestions);
}

// Update statistics
function updateStats(questionsPerDay, completedQuestions) {
    const weeklyTotal = Object.values(questionsPerDay).reduce((sum, val) => sum + val, 0);
    const completedTotal = Object.values(completedQuestions).reduce((sum, val) => sum + val, 0);
    const percentage = weeklyTotal > 0 ? Math.round((completedTotal / weeklyTotal) * 100) : 100;

    document.getElementById('target-total').textContent = `Target: ${weeklyTotal}`;
    document.getElementById('done-total').textContent = `Done: ${completedTotal}`;
    document.getElementById('percentage').textContent = `${percentage}%`;
    document.getElementById('progress-fill').style.width = `${percentage}%`;
}

// Generate projection data based on the weekly schedule
function generateProjection(totalQuestions, alreadyCompleted) {
    const data = [];
    const labels = [];
    let totalCompleted = alreadyCompleted; // Start with already completed questions
    let day = 0;

    // Get current values from inputs
    const daysOfWeek = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    const schedule = {};

    daysOfWeek.forEach(day => {
        const input = document.getElementById(`target-${day}`);
        schedule[day] = input ? (parseInt(input.value) || 0) : 0;
    });

    // Add starting point with already completed questions
    data.push(totalCompleted);
    labels.push(`Start (${alreadyCompleted} already done)`);

    // Generate data until we reach the target
    while (totalCompleted < totalQuestions) {
        day++;
        const weekday = daysOfWeek[day % 7];
        const questionsToday = schedule[weekday];

        // Only add a data point if questions are completed that day
        if (questionsToday > 0) {
            totalCompleted = Math.min(totalCompleted + questionsToday, totalQuestions);

            data.push(totalCompleted);
            labels.push(`Day ${day} (${weekday})`);

            // If we've reached the total, break
            if (totalCompleted >= totalQuestions) {
                break;
            }
        }
    }

    return { data, labels, completionDay: day };
}

// Create chart
function createChart(totalQuestions, alreadyCompleted) {
    const { data, labels, completionDay } = generateProjection(totalQuestions, alreadyCompleted);

    // Calculate completion date information
    const weeksToComplete = Math.floor(completionDay / 7);
    const daysRemaining = completionDay % 7;

    // Update info box
    const completionEstimation = document.getElementById('completion-estimation');
    const alreadyCompletedInfo = document.getElementById('already-completed-info');
    const weeklyTotalInfo = document.getElementById('weekly-total-info');

    completionEstimation.textContent = `Estimated time to complete all ${totalQuestions} questions: ${weeksToComplete} weeks and ${daysRemaining} days`;
    alreadyCompletedInfo.textContent = `Starting with ${alreadyCompleted} questions already completed`;

    // Calculate weekly total
    const daysOfWeek = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    let weeklyTotal = 0;

    daysOfWeek.forEach(day => {
        const input = document.getElementById(`target-${day}`);
        weeklyTotal += input ? (parseInt(input.value) || 0) : 0;
    });

    weeklyTotalInfo.textContent = `Based on your weekly schedule of ${weeklyTotal} questions per week`;

    // Create simplified labels for better readability
    const simplifiedLabels = labels.map((label, index) => {
        if (index === 0) return 'Start';
        if (index === labels.length - 1) return `Day ${completionDay}`;

        // Extract the day number from the label
        const dayMatch = label.match(/Day (\d+)/);
        if (dayMatch && dayMatch[1]) {
            return `Day ${dayMatch[1]}`;
        }
        return label;
    });

    // Create chart
    const ctx = document.getElementById('completionChart').getContext('2d');

    // Create data for goal line
    const goalData = Array(data.length).fill(totalQuestions);

    return new Chart(ctx, {
        type: 'line',
        data: {
            labels: simplifiedLabels,
            datasets: [
                {
                    label: 'Questions Completed',
                    data: data,
                    borderColor: '#3b82f6',
                    backgroundColor: 'rgba(59, 130, 246, 0.1)',
                    tension: 0.1,
                    pointRadius: 5,
                    pointHoverRadius: 7
                },
                {
                    label: `Goal (${totalQuestions} questions)`,
                    data: goalData,
                    borderColor: 'red',
                    borderDash: [5, 5],
                    pointRadius: 0,
                    fill: false
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: false,
                    min: 0,
                    max: totalQuestions + 10,
                    title: {
                        display: true,
                        text: 'Questions Completed'
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Days'
                    },
                    ticks: {
                        // Only show some of the labels to avoid overcrowding
                        callback: function(value, index, values) {
                            // Show first, last, and every 7th label (weekly intervals)
                            if (index === 0 || index === labels.length - 1 || index % 7 === 0) {
                                return simplifiedLabels[index];
                            }
                            return null;
                        },
                        maxRotation: 45,
                        minRotation: 45
                    }
                }
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return `${context.dataset.label}: ${context.raw} questions`;
                        },
                        title: function(tooltipItems) {
                            // Show the original detailed label in the tooltip
                            const index = tooltipItems[0].dataIndex;
                            return labels[index];
                        }
                    }
                }
            }
        }
    });
}

// Replace your existing JavaScript with this
document.addEventListener('DOMContentLoaded', function() {
    createVisualization();

    // Add resize listener with debounce
    let resizeTimer;
    window.addEventListener('resize', function() {
        clearTimeout(resizeTimer);
        resizeTimer = setTimeout(function() {
            createVisualization();
        }, 250); // Debounce resize events for better performance
    });

    createPredictionVisualization();
});