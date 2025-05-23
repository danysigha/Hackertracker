document.addEventListener("DOMContentLoaded", function() {

    // const customSelects = document.querySelectorAll("select");

    const searchBar = document.getElementById("search");

    let selectTopics;

    let selectTags;

    let selectDifficultyLevel;

    let selectStatus;

    let listOfProblems;

    const deleteBtn = document.getElementById('delete');

    const searchBtn = document.getElementById("searchBtn");

    const searchQueryBtns = Array.from(document.getElementsByClassName("searchQueryBtn"));

    const overlay = document.getElementById("overlay");

    const searchSectionGroup = document.getElementById("s010");

    const searchResultsGroup = document.getElementById("search-results");

    const advancedSearchGroup = document.getElementById("advance-search");

    const resultList = document.getElementById("result-list");

    const paginationNumbers = document.getElementById("pagination-numbers");

    const paginationContainer = document.getElementById("pagination-container");

    $.ajax({
        url: "api/search/tags",
        method: "GET",
        data: {},
        xhrFields: {
            withCredentials: true
        },
        success: function(data) {

            // console.log(data);

            let tagOptions = "<optgroup label='Tags'> <option placeholder='' value=''>Tags</option>";
            for (let i = 0; i < data.length; i++) {
                // if (i === 0) {
                //     tagOptions += `<option value="${data[i].tagName}" selected>${data[i].tagName}</option>`
                // } else {
                //     tagOptions += `<option value="${data[i].tagName}" >${data[i].tagName}</option>`
                // }
                tagOptions += `<option value="${data[i].tagName}" >${data[i].tagName}</option>`
            }
            tagOptions += "</optgroup>";

            $("#select-tags").html(tagOptions);

            selectTags = new TomSelect("#select-tags",{
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

            // console.log(data);

            let topicOptions = "<optgroup label='Topics'>";
            for(let i = 0; i < data.length; i++) {
                // if (i === 0) {
                //     topicOptions += `<option value="${data[i].topicName}" selected>${data[i].topicName}</option>`
                // } else {
                //     topicOptions += `<option value="${data[i].topicName}" >${data[i].topicName}</option>`
                // }
                topicOptions += `<option value="${data[i].topicName}" >${data[i].topicName}</option>`
            }
            topicOptions += "</optgroup>";

            $("#select-topics").html(topicOptions);

            selectTopics = new TomSelect("#select-topics",{
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

    selectDifficultyLevel = new TomSelect("#select-level",{
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

    selectStatus = new TomSelect("#select-status",{
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


    searchQueryBtns.forEach( btn => {
        btn.addEventListener("click", function(e) {
            e.preventDefault();
            showResults();
        })
    })


    document.addEventListener("mousedown", function (e) {
        if (searchResultsGroup.style.display !== "none" && overlay.style.display !== "none" && advancedSearchGroup.style.display === "none") {
            // Do NOT use preventDefault here
            if (!searchResultsGroup.contains(e.target) && e.target !== searchResultsGroup) {
                clearResults();
            }
        }

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

    deleteBtn.addEventListener("click", function (e){
        e.preventDefault();
        clearFields();
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

    function clearResults() {
        // Hide results and show advance search
        searchResultsGroup.style.display = 'none';
        resultList.innerHTML = "";
        paginationNumbers.innerHTML = "";
        advancedSearchGroup.style.display = 'block';
    }

    function clearFields() {
        selectTags.clear();
        selectTopics.clear();
        selectDifficultyLevel.clear();
        searchBar.value = "";
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

                // console.log(title);
                // console.log(topics);
                // console.log(tags);
                // console.log(difficultyLevels);
                // console.log(status);
                //
                // console.log(data);

                listOfProblems = data;
                //
                let result = "";
                $("#advance-search").css("display", "none");

                if( data.length > 0) {
                    for(let i = 0; i < data.length; i++) {
                        const completed = data[i].completed ? "✅ " : "⏳ ";
                        const difficulty = data[i].problem.difficultyLevel;
                        result += `<div class="search-result">
                                <span result-index="${i}">${completed}${data[i].problem.questionTitle}</span>
                                <span difficulty="${difficulty}">${difficulty}</span>
                            </div>`;
                    }

                    resultList.innerHTML = result;

                    let listOfProblemDivs = Array.from(document.getElementsByClassName("search-result"));

                    // console.log(listOfProblemDivs);

                    listOfProblemDivs.forEach( result => {
                        result.addEventListener("click", function (e) {
                            // console.log(listOfProblems[e.target.getAttribute("result-index")]);
                            // console.log(listOfProblems[e.target.getAttribute("result-index")].problem.problemId)
                            loadQuestion(listOfProblems[e.target.getAttribute("result-index")].problem.problemId, false);
                            // clearResults();
                            hideOverlay();
                        })
                    })

                    showPagination();
                } else {
                    resultList.innerHTML = `<p style="color: gray">NO MATCHES FOUND</p>`
                    resultList.style.display = "flex";
                    resultList.style.alignItems = "center";
                    resultList.style.justifyContent = "center";
                    resultList.style.height = "100%";
                    paginationContainer.style.display = "none";
                }

                $("#search-results").css("display", "flex");
            },
            error: function(xhr, status, error) {
                console.error("Error fetching query results:", error);
            }
        });
    }


    function showPagination() {
        const paginatedList = document.getElementById("search-results");
        const listItems = Array.from(paginatedList.getElementsByClassName("search-result"));
        const nextButton = document.getElementById("next-button");
        const prevButton = document.getElementById("prev-button");

        const paginationLimit = 20;
        const pageCount = Math.ceil(listItems.length / paginationLimit);
        let currentPage = 1;

        // console.log(listItems);

        const disableButton = (button) => {
            button.classList.add("disabled");
            button.setAttribute("disabled", true);
        };

        const enableButton = (button) => {
            button.classList.remove("disabled");
            button.removeAttribute("disabled");
        };

        const handlePageButtonsStatus = () => {
            if (currentPage === 1) {
                disableButton(prevButton);
            } else {
                enableButton(prevButton);
            }

            if (pageCount === currentPage) {
                disableButton(nextButton);
            } else {
                enableButton(nextButton);
            }
        };

        const handleActivePageNumber = () => {
            document.querySelectorAll(".pagination-number").forEach((button) => {
                button.classList.remove("active");
                const pageIndex = Number(button.getAttribute("page-index"));
                if (pageIndex == currentPage) {
                    button.classList.add("active");
                }
            });
        };

        const appendPageNumber = (index) => {
            const pageNumber = document.createElement("button");
            pageNumber.className = "pagination-number";
            pageNumber.setAttribute("type", "button");
            pageNumber.innerHTML = index;
            pageNumber.setAttribute("page-index", index);
            pageNumber.setAttribute("aria-label", "Page " + index);

            paginationNumbers.appendChild(pageNumber);
        };

        const getPaginationNumbers = () => {
            for (let i = 1; i <= pageCount; i++) {
                appendPageNumber(i);
            }
        };

        const setCurrentPage = (pageNum) => {
            document.getElementById("search-results").scrollTo(0,0);

            currentPage = pageNum;

            handleActivePageNumber();
            handlePageButtonsStatus();

            const prevRange = (pageNum - 1) * paginationLimit;
            const currRange = pageNum * paginationLimit;

            listItems.forEach((item, index) => {
                item.style.display = "none";
                if (index >= prevRange && index < currRange) {
                    item.style.display = "flex";
                }
            });
        };

        getPaginationNumbers();
        setCurrentPage(1);
        paginationContainer.removeAttribute("style");
        resultList.removeAttribute("style");

        prevButton.addEventListener("click", () => {
            setCurrentPage(currentPage - 1);
        });

        nextButton.addEventListener("click", () => {
            setCurrentPage(currentPage + 1);
        });

        document.querySelectorAll(".pagination-number").forEach((button) => {
            const pageIndex = Number(button.getAttribute("page-index"));

            if (pageIndex) {
                button.addEventListener("click", () => {
                    setCurrentPage(pageIndex);
                });
            }
        });
    }

});



