var statusFlag = null;
var SN = null;
var resource = null;

function getResource() {
	if (resource == null) {
		// 取对应的语言文件
		resource = window.AppJsBridge.getDefaultResource();
	}
	return resource;
}

function initPage() {
	var spanArray = document.getElementsByTagName("span");
	for (var i = 0; i < spanArray.length; i++) {
		var key = spanArray[i].getAttribute("local_key");
		if (getResource()[key]) {
			spanArray[i].innerHTML = getResource()[key];
		}
	}
}

function resultBack(resultMsg) {
	// alert(resultMsg);
}

function load() {
	window.AppJsBridge.service.localeService.getResource({
		"success" : function(data) {
			resource = data;
			initPage();
		},
		"error" : function(data) {
		}
	})
}

function goBack() {
	window.AppJsBridge.goBack({
		"success" : function(data) {
		},
		"error" : function(data) {
		}
	});
}

function nextPage() {
	window.location.href = "dev_link.html";
	// window.location.href = "ap_bind.html";
}
