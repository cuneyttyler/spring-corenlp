var maxNode = 6;

var width = 960, height = 500;

var color = d3.scale.category20();

var force = d3.layout.force().gravity(.05).distance(200).charge(-500).size(
		[ width, height ]);

var nodes = []; // { "name" : "", "group" : "" }
var links = []; // { "source" : "", "target" : "", "value" : "" }

force.nodes(nodes).links(links).on("tick", tick);

$(document).ready(function() {
	console.log("ready");
});

function search(searchText) {
	if (window.svg != null) {
		console.log("svg = " + window.svg);
		window.svg.html(null);
	} else {
		window.svg = d3.select("#graph").append("svg");
	}

	var link = window.svg.selectAll(".link");
	var node = window.svg.selectAll(".node");

	var tmp = searchText.toLowerCase();
	d3.select("#artist_text").html(tmp);
	tmp = tmp.replace(" ", "+");
	tmp = escape(tmp);

	var url = "http://ws.audioscrobbler.com/2.0/?method=artist.getsimilar&artist="
			+ tmp + "&api_key=ecc2e1fc920c9fcbcc19ba8790570691&format=json";

	var response = httpGet(url);

	var json = JSON.parse(response);

	nodes.length = 0;
	links.length = 0;

	// Center Node
	var tag = json.similarartists.artist;

	// Center Image alma olayı
	url = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist="
			+ tmp + "&api_key=ecc2e1fc920c9fcbcc19ba8790570691&format=json";
	response = httpGet(url);

	var jsonInfo = JSON.parse(response);

	var centerUrl = getImageUrl(jsonInfo.artist.image);
	var centerImg = new Image();
	centerImg.src = centerUrl;
	centerImg.id = "c";
	centerImg.onload = function() {
		imgOnLoad(this);
	};
	var centerNode = {
		"id" : "node_c",
		"name" : json.similarartists["@attr"].artist,
		"group" : 1,
		"image" : centerImg
	};
	var test = force.nodes();
	nodes.push(centerNode);

	var groupIndex = 1;
	var nodeIndex = 1;

	/*
	 * var tags = [ ]; for(var i = 1; i < maxNode + 1; i++) {
	 * 
	 * 
	 * tmp = tag[i].name.replace(" ","+").toLowerCase(); tmp = escape(tmp); //
	 * url =
	 * "http://ws.audioscrobbler.com/2.0/?method=artist.getsimilar&artist=" +
	 * tmp + "&api_key=ecc2e1fc920c9fcbcc19ba8790570691&format=json"; // var
	 * resp = httpGet(url); // var json2 = JSON.parse(resp); // tags[i] =
	 * json2.similarartists; }
	 */
	/*
	 * for(var i = 0; i < maxNode; i++) { var tmpNode = { "name" : tag[i].name,
	 * "group" : groupIndex}; nodes.push(tmpNode); var tmpLink = { "source" :
	 * nodeIndex++, "target" : 0, "value" : i+1 }; links.push(tmpLink); }
	 */
	test = force.nodes();
	for (var i = 1; i < maxNode; i++) {
		var imageUrl = getImageUrl(tag[i].image);
		var image = new Image();
		image.src = imageUrl;
		image.id = i;
		image.onload = function() {
			imgOnLoad(this);
		};
		/*
		 * for(var j = nodeIndex; j >= 1; j--){ var tempTag = tags[j].artist;
		 * for(var k = 0; k < maxNode; k++){
		 * 
		 * if(tempTag[k].name == tag[nodeIndex].name){ var lnk = { "source" :
		 * nodeIndex, "target" : j, "value" : 1 }; links.push(lnk); } } }
		 */

		var tmpNode = {
			"id" : "node_" + i,
			"name" : tag[i].name,
			"group" : 1,
			"image" : image
		};
		nodes.push(tmpNode);
		test = force.links();
		var tmpLink = {
			"source" : nodeIndex++,
			"target" : 0,
			"value" : 10
		};
		links.push(tmpLink);
	}

	// ***********************************//
	test = force.nodes();
	start(link, node, window.svg);

	return false;
}

function start(link, node, svg) {

	var test = force.links();
	link = link.data(force.links());
	link.enter().append("line").attr("class", "link").style("stroke-width",
			function(d) {
				return Math.sqrt(d.value);
			});
	link.exit().remove();

	node = svg.selectAll(".node");

	node.remove();

	node = svg.selectAll(".node").data(nodes);

	node.enter().append("g").attr("id", function(d) {
		return d.id;
	}).attr("class", "node").call(force.drag);

	var clipPath = node.append("clipPath").attr("id", function(d) {
		return "clipPath_" + d.id;
	});

	clipPath.append("circle").attr("r", function(d) {
		if (d.image.height < d.image.width) {
			return d.image.height / 2;
		} else {
			return d.image.width / 2;
		}
	}).attr("cx", function(d) {
		return d.image.width / 2;
	}).attr("cy", function(d) {
		return d.image.height / 2;
	});

	/*
	 * node.append("circle") .attr("r",5) .attr("r", 10) .style("fill",
	 * "#FF6600");
	 */

	node.append("image").attr("width", function(d) {
		return d.image.width;
	}).attr("height", function(d) {
		return d.image.height;
	}).attr("xlink:href", function(d) {
		return d.image.src;
	}).attr('image-rendering', 'optimizeQuality').attr('x', function(d) {
		return d.x;
	}).attr('y', function(d) {
		return d.y;
	}).attr("clip-path", function(d) {
		return "url(#clipPath_" + d.id + ")";
	});

	node.append("text").attr("dx", function(d) {
		var circle = svg.select("#clipPath_" + d.id).select("circle");
		var tmp = getVisualLength(d.name) / 2;
		return circle.attr("cx") - tmp;
	}).attr(
			"dy",
			function(d) {
				var circle = svg.select("#clipPath_" + d.id).select("circle");
				var cy = parseInt(circle.attr("cy"));
				var r = parseInt(circle.attr("r"));
				var y = cy + r + 15;
				console.log("y = " + circle.attr("cy") + ", "
						+ circle.attr("r") + ", " + y);
				return y;
			}).attr("class", ".text").text(function(d) {
		return d.name;
	});

	/*
	 * node.on("mouseover",function (d,i){ d3.select(this).select("circle")
	 * .transition() .duration(500) .attr("r", 15)
	 * 
	 * 
	 * node.on("mouseout",function (d,i){ d3.select(this).select("circle")
	 * .transition() .duration(500) .attr("r", 10) });
	 */

	node.on("click", function(d, i) {
		var tmp = d.name.replace(" ", "+").toLowerCase();
		tmp = escape(tmp);

		// lastfm url
		var url = "http://www.lastfm.com.tr/music/" + tmp;

		if (d3.event.which == 1) {
			search(d.name);
		} else if (d3.event.which == 2) {
			var win = window.open(url, "lastfm");
			win.focus();
		}
	});

	var img = d3.select("image");
	img.on("click", function(d, i) {

	});

	node.exit().remove();

	force.start();
}

function tick() {
	var link = window.svg.selectAll(".link");
	var node = window.svg.selectAll(".node");

	link.attr("x1", function(d) {
		return d.source.x;
	}).attr("y1", function(d) {
		return d.source.y;
	}).attr("x2", function(d) {
		return d.target.x;
	}).attr("y2", function(d) {
		return d.target.y;
	});

	node.attr("transform", function(d) {
		return "translate(" + (d.x - d.image.width / 2) + ","
				+ (d.y - d.image.height / 2) + ")";
	});
}

// Kaynak :
// http://blog.mastykarz.nl/measuring-the-length-of-a-string-in-pixels-using-javascript/
// d3 selectionından offsetwidth'e dogrudan erişemediğimizden uzadı.
function getVisualLength(text) {
	var ruler = d3.select("#ruler"); 
	var ruler2 = ruler[0];
	ruler.html(text);
	var length = ruler2[0].offsetWidth;
	ruler.html(null); 
	return length;
}

function imgOnLoad(image) {
	console.log("image loaded : (" + image.id + ")" + image.width + ", "
			+ image.height);

	var img = svg.select("#node_" + image.id).select("image");
	var clipPath = svg.select("#clipPath_node_" + image.id).select("circle");
	var text = svg.select("#node_" + image.id).select("text");

	console.log("img width " + img.attr("width"));
	if (img.attr("width") == 0) {
		// Image
		img.attr("width", image.width);
		img.attr("height", image.height);

		// ClipPath
		if (image.height < image.width) {
			clipPath.attr("r", image.height / 2);
		} else {
			clipPath.attr("r", image.width / 2);
		}
		clipPath.attr("cx", image.width / 2);
		clipPath.attr("cy", image.height / 2);

		// Text
		text.attr("dx", function(d) {
			var tmp = getVisualLength(d.name) / 2;
			return clipPath.attr("cx") - tmp;
		});
		text.attr("dy", function(d) {
			var cy = parseInt(clipPath.attr("cy"));
			var r = parseInt(clipPath.attr("r"));
			var y = cy + r + 15;
			console.log("y = " + clipPath.attr("cy") + ", "
					+ clipPath.attr("r") + ", " + y);
			return y;
		});
		text.attr("class", ".text");
	}
}
function getImageUrl(image) {
	if (image != "undefined") {
		for (var i = 0; i < image.length; i++) {
			if (image[i].size == 'large') {
				var tmp = image[i];
				console.log(i + " = " + tmp["#text"]);
				return tmp["#text"];
			}
		}
	} else {
		return "";
	}
}