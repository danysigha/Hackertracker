import * as d3 from "https://cdn.jsdelivr.net/npm/d3@7/+esm";

function createVisualization() {

    $.ajax({
        url: "/api/progress/stats",
        method: "GET",
        data: {},
        dataType: "json",
        xhrFields: {
            withCredentials: true
        },
        success: function(data) {
            // Progress data
            const easyProgressData = {
                total: data.numberOfEasyQuestions,
                completed: data.numberOfEasyCompletedQuestions,
                color: "#A8DCAB", // Teal for Easy
                progressColor: "green"
            };

            const mediumProgressData = {
                total: data.numberOfMediumQuestion,
                completed: data.numberOfMediumCompletedQuestions,
                color: "#FFDBBB", // Amber for Medium
                progressColor: "#FFAB00"
            };

            const hardProgressData = {
                total: data.numberOfHardQuestions,
                completed: data.numberOfHardCompletedQuestions,
                color: "#FF7F7F", // Red for Hard
                progressColor: "#E53935"
            };

            // Totals
            const totalSolved = data.numberOfCompletedQuestions;
            const attempts = data.numberOfAttempts;

            drawElements(easyProgressData, mediumProgressData, hardProgressData, totalSolved, attempts);

        }
    });

}



function drawElements(easyProgressData, mediumProgressData, hardProgressData, totalSolved, attempts) {

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

    // Calculate progress end angles
    const easyProgressAngle = easyStartAngle + (easyEndAngle - easyStartAngle) * (easyProgressData.completed / easyProgressData.total);
    const mediumProgressAngle = mediumStartAngle + (mediumEndAngle - mediumStartAngle) * (mediumProgressData.completed / mediumProgressData.total);
    const hardProgressAngle = hardStartAngle + (hardEndAngle - hardStartAngle) * (hardProgressData.completed / hardProgressData.total);


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
    // const progressNumberGroup = svg.append("g")
    //     .attr("transform", `translate(${centerX + baseSize * 0.02},${centerY - baseSize * 0.25})`);


    // Add centered progress number
    const progressNumberGroup = svg.append("g")
        .attr("transform", `translate(${centerX},${centerY - baseSize * 0.25})`);

// Create a single text element that will contain both numbers
    const progressText = progressNumberGroup.append("text")
        .attr("text-anchor", "middle") // This ensures perfect centering
        .attr("dominant-baseline", "central")
        .attr("y", 0);

// Add completed number as first tspan
    progressText.append("tspan")
        .attr("class", "center-label--completed")
        .attr("style", `font-size: ${baseSize * 0.22}px`)
        .text("0");

// Add total number as second tspan
    progressText.append("tspan")
        .attr("class", "center-label-missing")
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

    animate(totalSolved, progressText, easyProgressData, mediumProgressData, hardProgressData, easyCount, mediumCount, hardCount);
}


function animate(totalSolved, progressText, easyProgressData, mediumProgressData, hardProgressData, easyCount, mediumCount, hardCount) {
    // Animate progress number
    const startCount = 0;
    const duration = 2000;

    const progressNumber = progressText.select("tspan:first-child");

    const timer = d3.timer(function(elapsed) {
        const progress = Math.min(1, elapsed / duration);
        const currentCount = Math.floor(progress * totalSolved);
        progressNumber.text(currentCount);

        if (progress === 1) timer.stop();
    });


// Animate count numbers for each difficulty
    d3.timer(function(elapsed) {
        const progress = Math.min(1, elapsed / duration);

        easyCount.text(Math.floor(progress * easyProgressData.completed));
        mediumCount.text(Math.floor(progress * mediumProgressData.completed));
        hardCount.text(Math.floor(progress * hardProgressData.completed));

        if (progress === 1) return true; // Stop timer
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
    renderTopics();
    addEventListeners();
});