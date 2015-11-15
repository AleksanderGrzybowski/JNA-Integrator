$(function () {
    
    var actionUrl = '/calculate';
    
    $('#calculate').click(function (e) {
        e.preventDefault();
        
        $.ajax({
            url: actionUrl,
            type: 'GET',
            data: {
                left: $('#left').val(),
                right: $('#right').val(),
                numberOfPoints: $('#points').val(),
                'function': $('#function').val()
            },
            success: function (data) {
                alert(data)
            }
        })
    });
});
