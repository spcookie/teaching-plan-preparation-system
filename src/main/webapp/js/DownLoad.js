export default class {
    static download(url, name, suffix) {
        const a = $('<a>')
            .attr('href', url)
            .attr('download', name + '.' + suffix)
        a.get(0).click()
    }
}