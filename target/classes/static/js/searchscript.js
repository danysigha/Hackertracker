document.addEventListener("DOMContentLoaded", function() {

    const customSelects = document.querySelectorAll("select");

    const deleteBtn = document.getElementById('delete');

    const searchBtn = document.getElementById("searchBtn");

    const searchQueryBtn = document.getElementById("searchQueryBtn");

    const overlay = document.getElementById("overlay");

    const searchSectionGroup = document.getElementById("s010");



    let resultlist;

    $.ajax({
        url: "api/search/tags",
        method: "GET",
        data: {},
        xhrFields: {
            withCredentials: true
        },
        success: function(data) {

            console.log(data);

            let tagOptions = "<optgroup label='Tags'> <option placeholder='' value=''>Tags</option>";
            for (let i = 0; i < data.length; i++) {
                if (i === 0) {
                    tagOptions += `<option value="${data[i].tagName}" selected>${data[i].tagName}</option>`
                } else {
                    tagOptions += `<option value="${data[i].tagName}" >${data[i].tagName}</option>`
                }
            }
            tagOptions += "</optgroup>";

            $("#select-tags").html(tagOptions);

            new TomSelect("#select-tags",{
                plugins: ['remove_button'],
                create: true,
                onItemAdd:function(){
                    this.setTextboxValue('');
                    this.refreshOptions();
                },
                render:{
                    option:function(data,escape){
                        return '<div><span>' + escape(data.value) + '</span></div>';
                    },
                    item:function(data,escape){
                        return '<div>' + escape(data.value) + '</div>';
                    }
                }
            });
        },
        error: function (xhr, status, error) {
            console.error("Error loading tags:", error);
        }
    })

    $.ajax({
        url: "api/search/topics",
        method: "GET",
        data: {},
        xhrFields: {
            withCredentials: true
        },
        success: function(data) {

            console.log(data);

            let topicOptions = "<optgroup label='Topics'>";
            for(let i = 0; i < data.length; i++) {
                if (i === 0) {
                    topicOptions += `<option value="${data[i].topicName}" selected>${data[i].topicName}</option>`
                } else {
                    topicOptions += `<option value="${data[i].topicName}" >${data[i].topicName}</option>`
                }
            }
            topicOptions += "</optgroup>";

            $("#select-topics").html(topicOptions);

            new TomSelect("#select-topics",{
                plugins: ['remove_button'],
                create: true,
                onItemAdd:function(){
                    this.setTextboxValue('');
                    this.refreshOptions();
                },
                render:{
                    option:function(data,escape){
                        return '<div><span>' + escape(data.value) + '</span></div>';
                    },
                    item:function(data,escape){
                        return '<div>' + escape(data.value) + '</div>';
                    }
                }
            });
        },
        error: function(xhr, status, error) {
            console.error("Error loading topics:", error);
        }
    })

    new TomSelect("#select-level",{
        plugins: ['remove_button'],
        create: true,
        onItemAdd:function(){
            this.setTextboxValue('');
            this.refreshOptions();
        },
        render:{
            option:function(data,escape){
                return '<div><span>' + escape(data.value) + '</span></div>';
            },
            item:function(data,escape){
                return '<div>' + escape(data.value) + '</div>';
            }
        }
    });

    new TomSelect("#select-status",{
        plugins: ['remove_button'],
        create: true,
        onItemAdd:function(){
            this.setTextboxValue('');
            this.refreshOptions();
        },
        render:{
            option:function(data,escape){
                return '<div><span>' + escape(data.value) + '</span></div>';
            },
            item:function(data,escape){
                return '<div>' + escape(data.value) + '</div>';
            }
        }
    });


    searchQueryBtn.addEventListener("click", function(e) {
        e.preventDefault();
        showResults();
    })


    document.addEventListener("mousedown", function (e) {
        if (overlay.style.display !== "none") {
            // Do NOT use preventDefault here
            if (!searchSectionGroup.contains(e.target) && e.target !== searchSectionGroup) {
                hideOverlay();
            }
        }
    })

    searchBtn.addEventListener("click", function (e) {
        e.preventDefault();
        showOverlay();
    })

// Function to enable overlay and disable body scrolling
    function showOverlay() {

        // Make overlay visible
        overlay.style.display = 'initial';

        // Disable scrolling on the body
        document.body.style.overflow = 'hidden';

        // Make only the overlay scrollable if needed
        overlay.style.overflow = 'auto';
    }

// Function to hide overlay and re-enable body scrolling
    function hideOverlay() {
        // Hide overlay
        overlay.style.display = 'none';

        // Re-enable scrolling on the body
        document.body.style.overflow = 'auto';
    }

    //function to fetch and show the results of the search
    function showResults() {
        // Get filter values
        const title = $("#search").val();
        const topics = $("#select-topics").val();
        const tags = $("#select-tags").val();
        const difficultyLevels = $("#select-level").val();
        const status = $("#select-status").val();

        $.ajax({
            url: "api/search/challenges",
            method: "GET",
            data: {
                title: title,
                topics: topics,
                tags: tags,
                difficultyLevels: difficultyLevels,
                status: status
            },
            xhrFields: {
                withCredentials: true
            },
            success: function(data) {

                console.log(title);
                console.log(topics);
                console.log(tags);
                console.log(difficultyLevels);
                console.log(status);

                console.log(data);

                resultlist = data;

                let result = "<div class='resultlist'>";

                for(let i = 0; i < data.length; i++) {
                    const completed = data[i].completed ? "✅ " : "⏳ ";
                    const difficulty = data[i].problem.difficultyLevel;
                    result += `<div class="search-result">
                 <span result-index="${i}">${completed}${data[i].problem.questionTitle}</span>
                 <span difficulty="${difficulty}">${difficulty}</span>
               </div>`;
                }

                result += "</div>"

                $("#advance-search").html(result);
            },
            error: function(xhr, status, error) {
                console.error("Error fetching query results:", error);
            }
        });
    }

});



