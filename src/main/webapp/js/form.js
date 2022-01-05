import PlanServer from "./PlanServer.js";
import Alert from "./Alert.js";
import DownLoad from "./DownLoad.js";

let $baseForm = $('#base-form')
let $baseInput = $baseForm.find('input')
let baseData = {}

let $detailUl = $('#detail ul')

$('.base-ok').click(() => {
    let baseDataList = $baseInput.map((index, val) => {
        return $(val).val()
    }).get()
    baseData = {
        totalCourses: baseDataList[0],
        semester: baseDataList[1],
        limitCredits: baseDataList[2]
    }
    let b = basicVerification()
    if (b) {
        PlanServer.setBaseData(baseData).then((val) => {
            if (val.ok) {
                Alert.tips('成功')
                val.json().then(({statue}) => {
                    if (statue) {
                        if ($('#detail ul').children().length) {
                            $detailUl.children().remove()
                        }
                        let $lis = creatList()
                        $detailUl.append($lis)
                    }
                })
            }
        })
    } else {
        Alert.error('请输入数据')
    }
})

function basicVerification () {
    let is = true
    for (const v of Object.values(baseData)) {
        if (v === '') {
            is = false
        }
    }
    return is
}

function creatList() {
    console.log('开始创建列表')
    let $lis = []
    for (let i = 0; i < baseData.totalCourses; i++) {
        let $text = $('<input>').attr('type', 'text')
        let $number = $('<input>').attr('type', 'number')
        let $li = $('<li>')
        $li.append($text, $number)
        $lis.push($li)
    }
    return $lis
}

let courseNumberList = []

$('.detail-ok').click(() => {
    $('#relationship').find('table').empty()
    let map = []
    let $li = $detailUl.children()
    for (let i = 0; i < $li.length; i++) {
        let $inputBox = $($li[i]).children()
        let [name, credit] = $inputBox.map((index, val) => {
            return $(val).val()
        }).get()
        map.push({
            name,
            credit
        })
    }
    let verification = courseDetailsVerification(map)
    if (verification.statue) {
        PlanServer.setDetailData(map).then((resp) => {
            if (resp.ok) {
                Alert.tips('成功')
                resp.json().then(value => {
                    courseNumberList = []
                    for (let v of Object.values(value)) {
                        courseNumberList.push(v)
                    }
                    let $table = createMapId(value)
                    $('#course-number div').empty().append($table)
                })
            }
        })
    } else {
        Alert.error(verification.message)
    }
})

function courseDetailsVerification(data) {
    for (let datum of data) {
        if (datum.name === '') {
            return {
                statue: false,
                message: '课程名不能为空'
            }
        } else if (Number.parseInt(datum.credit) <= 0 || Number.parseInt(datum.credit) > baseData.limitCredits) {
            return {
                statue: false,
                message: '学分超过范围'
            }
        }
    }
    return {
        statue: true,
        message: ''
    }
}

function createMapId(map) {
    console.log('开始创建课程编号')
    let $table = $('<table>')
    let $thead = $('<thead>')
    let $tbody = $('<tbody>')
    let $trHead = $('<tr>')
    $trHead.append($('<th>').text('课程名'), $('<th>').text('编号'))
    $thead.append($trHead)
    for (const k of Object.keys(map)) {
        let name = $('<td>').text(k)
        let id = $('<td>').text(map[k])
        let $tr = $('<tr>')
        $tr.append(name, id)
        $tbody.append($tr)
    }
    $table.append($thead, $tbody)
    return $table
}

let $shipUl = $('#relationship ul')

$('#add-ship').click(() => {
    createMapInputBox($shipUl)
})

function createMapInputBox($ul) {
    let $li = $('<li>')
    let $input = $('<input>').attr('type', 'text')
    $li.append($input, $input.clone())
    $ul.append($li)
}

$('.ship-ok').click(() => {
    let $shipLi = $shipUl.children()
    let allShipMap = []
    for (let i = 0; i < $shipLi.length; i++) {
        let shipMap = $($shipLi[i]).children().map((index, value) => {
            return $(value).val()
        }).get()
        if (shipMap[0] !== '' && shipMap[1] !== '') {
            allShipMap.push(shipMap)
        }
    }
    let b = curriculumRelationshipVerification(allShipMap)
    if (b) {
        Alert.tips('成功')
        PlanServer.setShipData(allShipMap).then((resp) => {
            if (resp.ok) {
                resp.json().then((val) => {
                    console.log(val.message)
                })
            }
        })
    } else {
        Alert.error('关系输入错误')
    }
})

function curriculumRelationshipVerification(arrayMap) {
    for (let e of arrayMap) {
        for (let val of e) {
            let isLegitimate = false
            for (let ce of courseNumberList) {
                if (ce === val) {
                    isLegitimate = true
                    break
                }
            }
            if (!isLegitimate) {
                return false
            }
        }
    }
    return true
}

$('#strategy-ok').click(() => {
    let formData = new FormData($('#preparation-strategy').find('form').get(0));
    let val = formData.get('strategy')
    let strategyId
    switch (val) {
        case 'front' :
            strategyId = 0
            break
        case 'uniformity' :
            strategyId = 1
            break
    }
    PlanServer.generateTimetable(strategyId).then((resp) => {
        if (resp.ok) {
            resp.json().then((val) => {
                console.log('编制信息：',val)
                Alert.tips('成功')
                let $button = generateExportButton()
                let $timetable = generateTimetable(val)
                $('#timetable')
                    .css('display', 'flex')
                    .empty()
                    .append($timetable, $button)
                exportButtonClick()
            })
        } else if (resp.status === 500) {
            Alert.error('课程之间存在循环关系，无法编制')
        }
        else {
            Alert.error('输入的课程关系有误，无法编排课程')
        }
    })
})

function generateTimetable(map) {
    let $table = $('<table>')
    let $thead = $('<thead>')
    let $tr = $('<tr>')
    let $semester = $('<td>').text('学期')
    let $curriculum = $('<td>').text('课程')
    $tr.append($semester, $curriculum)
    $thead.append($tr)
    let $tbody = $('<tbody>')
    for (const [k, v] of Object.entries(map)) {
        let $num = $('<td>').text(Number.parseInt(k) + 1)
        let names = []
        for (let e of v) {
            names.push(e.name)
        }
        let $val = $('<td>').text(names.join(','))
        let $tr = $('<tr>')
        $tr.append($num, $val)
        $tbody.append($tr)
    }
    $table.append($thead, $tbody)
    return $table
}

function generateExportButton() {
    return $('<button>')
        .text('导出Excel')
        .attr('class', 'button-rectangle')
        .attr('id', 'export-ok')
}

function exportButtonClick() {
    $('#export-ok').click(() => {
        PlanServer.exportTimetable().then((resp) => {
            if (resp.ok) {
                resp.json().then((url) => {
                    console.log('url -> ', url)
                    DownLoad.download(url, '课程表', 'xlsx')
                })
            }
        })
    })
}