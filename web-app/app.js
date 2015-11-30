function getParams() {
    return {
        left: Number($('#left').val()),
        right: Number($('#right').val()),
        numberOfPoints: Number($('#points').val()),
        func: $('#func').val()
    }
}

$(function () {
    var actionUrl = '/calculate';

    $('#calculate').click(function (e) {
        e.preventDefault();

        var params = getParams();
        var span = params.right - params.left;
        
        if (span < 0) {
            alert('Invalid range');
            return;
        }

        functionPlot({
            width: $('#plot-container').width(),
            height: $(window).height() / 2, // TODO better?
            target: '#plot',
            xDomain: [params.left - 1, params.right + 1],
            data: [
                { // definite integral
                    fn: params.func,
                    range: [params.left, params.right],
                    closed: true,
                    yDomain: [-1, 1],
                    xDomain: [-10, 10]
                },
                { // outline
                    fn: params.func,
                    range: [params.left - span, params.right + span],
                    yDomain: [-1, 1],
                    xDomain: [-10, 10]
                }
            ]
        });

        $.ajax({
            url: actionUrl,
            type: 'GET',
            data: params,
            success: function (data) {
                var $result = $('#result');
                
                $result.text("$$ \\int_{" + params.left + "}^{" + params.right + "}" + params.func + " = " + data.result + " $$");
                $result.removeClass('animate-fadeInOut');
                MathJax.Hub.Typeset();
            },
            error: function () {
                $('#result').text('Cannot perform calculation.');
            }
        })
    });
});

