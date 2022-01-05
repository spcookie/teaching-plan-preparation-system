export default class Alert {
    static tips(text) {
        appear(text, 'rgb(64,158,255)')
    }

    static error(text) {
        appear(text, 'rgb(96,98,102)')
    }
}

let css = {
    opacity: 0,
    position: 'fixed',
    top: 0,
    left: '50%',
    transform: 'translateX(-50%)',
    padding: '0 10px',
    'min-height':'40px',
    'min-width': '10%',
    'max-width': '50%',
    'border-radius': '10px',
    'font-size': '32px',
    color: 'white',
    'text-align': 'center',
    'line-height': '40px',
    'z-index': 9999,
}

function appear(text, color) {
    const $body = $('body')
    const $div = $('<div>').css(css).attr('class', 'message')
    $div.text(text)
    $div.css('background-color', color)
    $div.animate({
        opacity: 1,
        top: '4%'
    }, 300).queue(() => {
        setTimeout(() => {
            $div.css({
                opacity: 0,
                bottom: 0
            })
            $div.dequeue()
            setTimeout(() => {
                $div.remove('.message')
            }, 10)
        }, 1000)
    })
    $body.append($div)
}