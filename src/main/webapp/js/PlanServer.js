import BaseApi from './BaseApi.js'

export default class PlanServer {
    static setBaseData(data) {
        const url = '/input/inputBaseData'
        return BaseApi.post(url, data)
    }

    static setDetailData(data) {
        const url = '/input/inputDetailData'
        return BaseApi.post(url, data)
    }

    static setShipData(data) {
        const url = '/input/inputShipData'
        return BaseApi.post(url, data)
    }

    static generateTimetable(query) {
        const url = '/input/generateTimetable?strategy=' + query
        return BaseApi.get(url)
    }

    static exportTimetable() {
        const url = '/export/excel'
        return BaseApi.get(url)
    }
}