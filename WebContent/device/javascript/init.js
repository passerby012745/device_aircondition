;(function(win){ 
	var dpr, rem, scale;
	var docEl = document.documentElement;
	var fontEl = document.createElement('style');
	var metaEl = document.querySelector('meta[name="viewport"]');
	dpr = win.devicePixelRatio || 1;
	rem = docEl.clientWidth * dpr / 10;
	scale = 1 / dpr; 
	// 给js调用的，某一dpr下rem和px之间的转换函数
	win.rem2px = function(v) {
	    v = parseFloat(v);
	    return v * rem;
	};
	win.px2rem = function(v) {
	    v = parseFloat(v);
	    return v / rem;
	};
	win.dpr = dpr;
	win.rem = rem; 
	
	
	// 平台、设备和操作系统
	var system ={
		win : false,
		mac : false,
		xll : false
	};

	// 检测平台
	var p = navigator.platform;
	system.win = p.indexOf("Win") == 0;
	system.mac = p.indexOf("Mac") == 0;
	system.x11 = (p == "X11") || (p.indexOf("Linux") == 0);
	 
	if(system.win||system.mac||system.xll){  
		docEl.firstElementChild.appendChild(fontEl);
		fontEl.innerHTML = 'body{max-width:1080px;width:100%;margin:0 auto}';
	}else{
		// 设置viewport，进行缩放，达到高清效果
		metaEl.setAttribute('content', 'width=' + dpr * docEl.clientWidth + ',initial-scale=' + scale + ',maximum-scale=' + scale + ',minimum-scale=' + scale + ',user-scalable=no');
		// 设置data-dpr属性，留作的css hack之用
		docEl.setAttribute('data-dpr', dpr);
		// 动态写入样式
		docEl.firstElementChild.appendChild(fontEl);
		var _fontSize = (docEl.clientWidth / 10)<108?(docEl.clientWidth / 10):108;
		fontEl.innerHTML = 'html{font-size:' + _fontSize + 'px!important;}';
	} 	
})(window); 
