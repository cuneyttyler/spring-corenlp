//////////////////////////////////////////////////
/// Tile Town /////
/// by Andrew R. Brown - http://andewrbrown.net.au
/// August 2015
//////////////////////////////////////////////////


var cnvs = document.getElementById("area");
// 27 inch iMac w 2540, h 1420
cnvs.width = window.innerWidth - 20; //screen.width - 20;
cnvs.height = window.innerHeight - 20; //screen.height - 20;
var ctx = cnvs.getContext("2d");

// paramters
var width = cnvs.width;
var height = cnvs.height;
var dist = 40;
var roadDensity = 0;
var wipeDensity = 0;
//var wipeDensity2 = 0;
var wipeRate = 4;
var delay = 2000; // speed of evolution
var denMax = 5;
var lineEndProb = 0.8;
var branchProb = 0.5;
var buildProb = 0.8;

// utilities

function rndInt(min, max) {
    return Math.floor(Math.random() * (max + 1 - min) + min);
};

var dev = function(val, d) {
    return val + rndInt(d * -1, d);
};

// roads
var drawRoadSeg = function(x1, y1, x2, y2, w, r, g, b, t) {
    ctx.strokeStyle = "rgba(" + r + "," + g + "," + b + "," + t + ")";
    ctx.lineWidth = w;
    ctx.lineCap = "butt";
    ctx.beginPath();
    ctx.moveTo(dev(x1, 2),  dev(y1, 2));
    ctx.lineTo(dev(x2, 2),  dev(y2, 2));
    ctx.stroke();
};

var layRoad = function(x1, y1, x2, y2) {
    drawRoadSeg(x1, y1, x2, y2, rndInt(4, 6) + 6, 200, 200, 200, 0.3);//228, 193, 158, 0.3);
    setTimeout(function () {drawRoadSeg(x1, y1, x2, y2,
            rndInt(2, 4) + 4, 114, 114, 114, 0.3)}, delay * 0.25); //114, 114, 0, 0.3
    setTimeout(function () {drawRoadSeg(x1, y1, x2, y2,
            rndInt(1, 2) + 2, 50, 50, 50, 0.7)}, delay * .75); //71, 71, 25, 0.7
    if (Math.random() < 0.05 && y1 < height - dist * 2 && x1 < width - dist * 2) {
        patch(x1, y1, parkColor());
        if (Math.random() < 0.2) tree(x1, y1);
    }
};

function doWipe(x1, y1, x2, y2, w, clr) {
    ctx.strokeStyle = clr;
    ctx.lineWidth = dist * w;
    ctx.lineCap = "square";
    ctx.beginPath();
    ctx.moveTo(x1, y1);
    ctx.lineTo(x2, y2);
    ctx.stroke();
}

var wipeRoad = function(x1, y1, x2, y2) {
    doWipe(x1, y1, x2, y2, 1.2, "rgba(255, 255, 255, 0.1)");
    setTimeout(function () {doWipe(x1, y1, x2, y2, 0.6, "rgba(255, 255, 255, 0.2)")},
            delay * 0.5);
    setTimeout(function () {doWipe(x1, y1, x2, y2, 0.3, "rgba(255, 255, 255, 0.5)")},
        delay);
    if (Math.random() < 0.5) patch(x1, y1, "rgba(255, 255, 255, 0.3)");
}

/*
 var wipeRoad2 = function(x1, y1, x2, y2) {
 doWipe(x1, y1, x2, y2, 1.3, "rgba(255, 255, 255, 0.2)");
 setTimeout(function () {doWipe(x1, y1, x2, y2, 1.0, "rgba(255, 255, 255, 0.6)")},
 delay * 0.4);
 setTimeout(function () {doWipe(x1, y1, x2, y2, 0.7, "rgba(255, 255, 255, 1.0)")},
 delay * 0.8);
 }
 */

function addDensity(func) {
    if (func === layRoad) {
        roadDensity += 1;
    } else if (func === wipeRoad) {
        wipeDensity += 1;
    } //else wipeDensity2 += 1;
}

function decDensity(func) {
    if (func === layRoad) {
        roadDensity -= 1;
    } else if (func === wipeRoad) {
        wipeDensity -= 1;
    } //else wipeDensity2 -= 1;
    //console.log(roadDensity + "," + wipeDensity + "," + wipeDensity2);
}

function getDensity(func) {
    if (func === layRoad) {
        return roadDensity;
    } else if (func === wipeRoad) {
        return wipeDensity;
    } /*else {
     return wipeDensity2;
     }*/
}

function buildConditions(x, y, func) {
    return (func === layRoad && Math.random() < buildProb && x < width - dist * 2
        && x > dist * 2 && y < height - dist * 2 && y > dist * 2);
}

function roadHR(x, y, func) {
    func(x, y, x + dist, y);
    if (getDensity(func) < denMax) {
        if (Math.random() < branchProb && y < height - dist) {
            setTimeout(function () {roadVD(x + dist, y, func)}, delay / 2);
            if(buildConditions(x, y, func)) building(x + 4, y, 0, bldColor());
            addDensity(func);
        }
        if (Math.random() < branchProb && y > dist * 2) {
            setTimeout(function () {roadVU(x + dist, y, func)}, delay  / 4);
            if(buildConditions(x, y, func)) building(x + 4, y - dist, 0, bldColor());
            addDensity(func);
        }
    }
    if (x + dist < width - dist * 3 && Math.random() < lineEndProb) {
        var del = delay;
        if (func === wipeRoad) del = delay * wipeRate;
        if (Math.random() < 0.5 && func === layRoad) del = delay / 2.0;
        setTimeout(function () {roadHR(x + dist, y, func)}, del);
    } else {
        decDensity(func);
        if (getDensity(func) === 0) start(func);
    }
}

function roadHL(x, y, func) {
    func(x, y, x - dist, y);
    if (getDensity(func) < denMax) {
        if (Math.random() < branchProb && y < height - dist) {
            setTimeout(function () {roadVD(x - dist, y, func)}, delay / 2);
            if(buildConditions(x, y, func)) building(x - dist + 4, y, 0, bldColor());
            addDensity(func);
        }
        if (Math.random() < branchProb && y > dist * 2) {
            setTimeout(function () {roadVU(x - dist, y, func)}, delay / 4);
            if(buildConditions(x, y, func)) building(x - dist + 4, y - dist, 0, bldColor());
            addDensity(func);
        }
    }
    if (x - dist > dist * 3 && Math.random() < lineEndProb) {
        var del = delay;
        if (func === wipeRoad) del = delay * wipeRate;
        if (Math.random() < 0.5 && func === layRoad) del = delay / 2.0;
        setTimeout(function () {roadHL(x - dist, y, func)}, del);
    } else {
        decDensity(func);
        if (getDensity(func) === 0) start(func);
    }
}

//roadHL(300, 200, layRoad);

function roadVD(x, y, func) {
    func(x, y, x, y + dist);
    if (getDensity(func) < denMax) {
        if (Math.random() < branchProb && x < width - dist * 2) {
            setTimeout(function () {roadHR(x, y + dist, func)}, delay / 2);
            if(buildConditions(x, y, func)) building(x + 4, y, 0, bldColor());
            addDensity(func);
        }
        if (Math.random() < branchProb && x > dist * 2) {
            setTimeout(function () {roadHL(x, y + dist, func)}, delay / 4);
            if(buildConditions(x, y, func)) building(x - dist + 2, y, 0, bldColor());
            addDensity(func);
        }
    }
    if (y + dist < height - dist * 3 && Math.random() < lineEndProb) {
        var del = delay;
        if (func === wipeRoad) del = delay * wipeRate;
        if (Math.random() < 0.5 && func === layRoad) del = delay / 2.0;
        setTimeout(function () {roadVD(x, y + dist, func)}, del);
    } else {
        decDensity(func);
        if (getDensity(func) === 0) start(func);
    }
}

//addDensity(layRoad);
//roadVD(200, 200, layRoad);

function roadVU(x, y, func) {
    func(x, y, x, y - dist);
    if (getDensity(func) < denMax) {
        if (Math.random() < branchProb && x < width - dist * 2) {
            setTimeout(function () {roadHR(x, y - dist, func)}, delay / 2);
            if(buildConditions(x, y, func)) building(x + 2, y - dist + 2, 0, bldColor());
            addDensity(func);
        }
        if (Math.random() < branchProb && x > dist * 2) {
            setTimeout(function () {roadHL(x, y - dist, func)}, delay / 4);
            if(buildConditions(x, y, func)) building(x - dist + 4, y - dist, 0, bldColor());
            addDensity(func);
        }
    }
    if (y - dist > dist * 3 && Math.random() < lineEndProb) {
        var del = delay;
        if (func === wipeRoad) del = delay * wipeRate;
        if (Math.random() < 0.5 && func === layRoad) del = delay / 2.0;
        setTimeout(function () {roadVU(x, y - dist, func)}, del);
    } else {
        decDensity(func);
        if (getDensity(func) === 0) start(func);
    }
}

// buildings

function bldColor () {
    var color = "rgba(10, " + rndInt(30, 80) + ", 100," + (Math.random()/4.0 + 0.3) + ")"; // blue
    if (Math.random() < .5)
        color = "rgba(" + rndInt(30, 120) + ", 30, " + rndInt(10, 50) + ", " +
            (Math.random()/4.0 + 0.3) + ")"; // red
    return color;
}

function building(x, y, i, cl) {
    var offset = 3;
    ctx.strokeStyle = cl
    ctx.lineWidth = dist * .7;
    ctx.lineCap = "butt";
    ctx.beginPath();
    ctx.moveTo(x + offset * i, y + dist/2 - offset * i);
    ctx.lineTo(x + dist * .7 + offset * i, y + dist/2 - offset * i);
    ctx.stroke();
    if (Math.random() < 0.8 && i < 20 && y > dist * 2)
        setTimeout(function () {building(x, y, i + 1, cl)}, delay / 2.0);
}

// parks

function patch(x, y, c) {
    var offset = 0;
    ctx.strokeStyle = c;
    ctx.lineWidth = dist + rndInt(3, 6);
    ctx.lineCap = "butt";
    ctx.beginPath();
    ctx.moveTo(dev(x, 2) + offset, dev(y, 2) + dist/2 - offset);
    ctx.lineTo(dev(x, 2) + dist + rndInt(3, 6) + offset,
            dev(y, 2) + dist/2 - offset);
    ctx.stroke();
}

function parkColor() {
    return "rgba(" + rndInt(0, 100) + ", 100, 20," + (Math.random()*0.3 + 0.2) + ")";
}

function park(x, y) {
    patch(x, y, parkColor());
    setTimeout(function () {patch(x, y, parkColor())}, delay);
    if (Math.random() < 0.5 && x < width - 2 * dist) {
        setTimeout(function () {tree(x, y)}, delay * 0.5);
    }
    var nextX = x;
    if (Math.random() < 0.3) {
        nextX = x + dist;
    } else if (Math.random() < 0.3) {
        nextX = x - dist;
    }
    var nextY = y;
    if (Math.random() < 0.3) {
        nextY = y + dist;
    } else if (Math.random() < 0.3) {
        nextY = y - dist;
    }
    if (nextX > width - dist || nextX < dist || Math.random() < 0.05) {
        nextX = rndInt(2, width / dist - 4) * dist;
    }
    if (nextY > height - dist || nextY < dist || Math.random() < 0.05) {
        nextY = rndInt(2, height / dist - 4) * dist;
    }
    setTimeout(function () {park(nextX, nextY)}, delay * 2);
    //setTimeout(function () {park((Math.ceil(Math.random() * (width / dist - 4)) + 2) * dist,
    //       (Math.ceil(Math.random() * (height / dist - 4)) + 2) * dist)}, delay);
}

function branch(x, y, len, dev, w) {
    var xe = x + len + rndInt(dev * -1.5, dev * 2.5);
    var ye = y - len - rndInt(0, dev);
    ctx.strokeStyle = "rgba(" + rndInt(20, 50) + ", " + rndInt(30, 80) + ", 0, 0.6)";
    ctx.lineWidth = w;
    ctx.beginPath();
    ctx.moveTo(x, y);
    ctx.lineTo(xe, ye);
    ctx.stroke();
    if (len > 3) {
        for(var i=0; i<3; i++) {
            setTimeout(function () {branch(xe, ye, len * 0.78, dev + rndInt(-1, w * 2), w * 0.5)},
                    delay * Math.random() * 2 + 0.25);
        }
    }
}

function tree(x, y) {
    branch(x + dist/2, y + dist/2, 6, 0, 4);
}


// decay

function clearAll() {
    ctx.fillStyle = "rgba(255, 255, 255, 0.1)";
    ctx.fillRect(0, 0, width, height);
    setTimeout(function () {clearAll()}, delay * 25);
}

clearAll();

// Loop over each pixel and clear
function clearArea(x, y, xSize, ySize) {
    var imgd = ctx.getImageData(x, y, xSize, ySize);
    var pix = imgd.data;
    var thresh = 180;
    for (var i = 0, n = pix.length; i < n; i += 4) {
        if (pix[i] > thresh && pix[i+1] > thresh && pix[i+2] > thresh) {
            ctx.clearRect(x + ((i / 4) % xSize), y + Math.floor(i / 4 / xSize), 2, 2);
        }
    }
}

function rndClear() {
    clearArea(rndInt(0, width / dist) * dist - dist * 0.2,
            rndInt(0, height / dist) * dist - dist * 0.2,
            dist * 1.2, dist * 1.2);
    setTimeout(function () {rndClear()}, delay / 32);
}

rndClear();

function nearClear(x, y) {
    clearArea(rndInt(0, width / dist) * dist - dist * 0.2,
            rndInt(0, height / dist) * dist - dist * 0.2,
            dist * 1.2, dist * 1.2);
    var nextX = x;
    if (Math.random() < 0.3) {
        nextX = x + dist;
    } else if (Math.random() < 0.3) {
        nextX = x - dist;
    }
    var nextY = y;
    if (Math.random() < 0.3) {
        nextY = y + dist;
    } else if (Math.random() < 0.3) {
        nextY = y - dist;
    }
    if (nextX > width - dist || nextX < dist || Math.random() < 0.05) {
        nextX = rndInt(2, width / dist - 4) * dist;
    }
    if (nextY > height - dist || nextY < dist || Math.random() < 0.05) {
        nextY = rndInt(2, height / dist - 4) * dist;
    }
    setTimeout(function () {nearClear(nextX, nextY, dist * 1.2, dist * 1.2)}, delay * 0.25);
}

nearClear(rndInt(0, width / dist) * dist - dist * 0.2,
        rndInt(0, height / dist) * dist - dist * 0.2,
        dist * 1.2, dist * 1.2);

nearClear(rndInt(0, width / dist) * dist - dist * 0.2,
        rndInt(0, height / dist) * dist - dist * 0.2,
        dist * 1.2, dist * 1.2);

// main

function start(func) {
    addDensity(func);
    roadVD(rndInt(2, width / dist - 4) * dist, rndInt(2, height / dist - 4) * dist, func);
}

start(layRoad);
start(wipeRoad);
start(wipeRoad);
start(wipeRoad);
for (var i=0; i<3; i++) {
    park(rndInt(2, width / dist - 4) * dist, rndInt(2, height / dist - 4) * dist);
}

// music playback

var audio = document.createElement('audio');
audio.src = "static/visual/resources/TileTownAudio-loop.mp3";
audio.loop = true;
audio.play();




/*
 var musicTrack = new Audio();

 musicTrack.onopen = function () {
 musicTrack.loop = true;
 musicTrack.play();
 }

 musicTrack = new Audio("TileTownAudio-loop.mp3");
 */