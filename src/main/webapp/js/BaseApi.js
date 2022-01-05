const baseUrl = ''

export default class BaseApi {
    static get(url) {
        return fetch(url)
    }

    static async post(url, body) {
        return fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
            body: JSON.stringify(body)
        })
    }
}