
$(document).ready(function () {
    $.ajax({
        url: API_BLOG_TAG_AUTOCOMPLETE_ALL,
        success: function (response) {
            var jsonData = [];
            var tags = response;
            for (var i = 0; i < tags.length; i++) jsonData.push({id: i, name: tags[i]});
            var tag_input_element = $('#tag_input_element').tagSuggest({
                data: jsonData,
                sortOrder: 'name',
                maxDropHeight: 200,
                name: 'tag_input_element',
                maxSelection: null,
                emptyText: jQuery("div[id='global.add']").html(),
                selectionPosition: "bottom",
                typeDelay: 0,
                strikeA_Match: true
            });

        }
    });

});

var tags = $('#tag_input_element').tagSuggest().getSelectedItems().map(function (item) {
    return item['name'];
});

// <input class="" id="tag_input_element" type="text" name="tag_input_element" width="200"/>

tinymce.init({
    selector: 'textarea',
    entity_encoding: "raw",
    height: 500,
    menubar: false,
    plugins: [
        'advlist autolink lists link image charmap print preview anchor',
        'searchreplace visualblocks code fullscreen',
        'insertdatetime media table contextmenu paste code'
    ],
    toolbar: 'undo redo | insert | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image',
    content_css: '//www.tinymce.com/css/codepen.min.css'
});

