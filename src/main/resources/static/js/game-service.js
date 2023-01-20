import {simpleHttpClient, httpClientConfiguration} from "./simple-http-client.js";

const futrzakGameService = {
    confirmWin: function (winConfirmationRequest) {
        return simpleHttpClient.post(httpClientConfiguration.apiBaseUrl + "game/win", JSON.stringify(winConfirmationRequest));
    }
}

export { futrzakGameService };