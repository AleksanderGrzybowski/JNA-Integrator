$(function () {
    
    var actionUrl = '/calculate';
    
    $('#calculate').click(function (e) {
        e.preventDefault();

        var left = Number($('#left').val());
        var right = Number($('#right').val());
        var numberOfPoints = Number($('#points').val());
        var func = $('#function').val();
        
        var span = right - left;

        var $plot = $('#plot-target');
        var plotWidth = $plot.width();
        var plotHeight = $('body').height(); // TODO better?
        
        functionPlot({
            width: plotWidth,
            height: plotHeight,
            target: '#plot',
            xDomain: [left - 1, right + 1],
            data: [
                { // definite integral
                    fn: func,
                    range: [left, right],
                    closed: true,
                    yDomain: [-1, 1],
                    xDomain: [-10, 10]
                },
                {
                    fn: func,
                    range: [left - span, right + span],
                    yDomain: [-1, 1],
                    xDomain: [-10, 10]
                },

            ]
        });

        $.ajax({
            url: actionUrl,
            type: 'GET',
            data: {
                left: left,
                right: right,
                numberOfPoints: numberOfPoints,
                'function': func
            },
            success: function (data) {
                $('#result').text("$$ \\int_{" + left + "}^{" + right + "}" + func + " = " + data.result + " $$");
                MathJax.Hub.Typeset();
            }
        })
    });
});

