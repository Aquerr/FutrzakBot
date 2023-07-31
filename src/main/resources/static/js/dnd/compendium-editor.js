document.addEventListener("load", new function () {
   document.getElementById("menu-items").addEventListener("click", loadItemsEntires);
   document.getElementById("menu-creatures").addEventListener("click", loadCreatureEntires)
});

function loadItemsEntires() {
    console.log("Loading items entries...");
}

function loadCreatureEntires() {
    console.log("Loading creatures entires...");
}