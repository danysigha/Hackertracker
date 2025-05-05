document.addEventListener("DOMContentLoaded", function() {

    const customSelects = document.querySelectorAll("select");

    const deleteBtn = document.getElementById('delete');

    const searchBtn = document.getElementById("searchBtn");

    const overlay = document.getElementById("overlay");

    const searchSectionGroup = document.getElementById("s010");

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

});



