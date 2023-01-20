const httpClientConfiguration = {
    apiBaseUrl: "http://localhost:8080/api/",
    contentType: "application/json"
}

const simpleHttpClient = {
    get: async function (url) {
        return await fetch(url,{
            url: `${url}`,
            mode: "cors",
            method: "GET",
            headers: {
                "Accept": "application/json"
            }
        })
    },

    post: async function (url, data) {
        return await fetch(url,{
            url: `${url}`,
            mode: "cors",
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: data
        })
    },

    delete: async function (url) {
        return await fetch(url,{
            url: `${url}`,
            mode: "cors",
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            }
        })
    }
};

export {simpleHttpClient, httpClientConfiguration};