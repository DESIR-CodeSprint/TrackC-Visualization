
var app = angular.module('desirApp', []);

var dataobject;
var scene;
var camera;
var renderer;
var container3D;
var ambientLight;
var directionalLight;
var pointsObject;
var pointSpheres = [];
var lineObjects = [];
var raycaster;
var currentIntersectedPoint;
var currentIntersectedPointIndex;
var currentIntersectedLine;
var currentIntersectedLineIndex;
var mouse;
var hooverPointSphere;
var pointSize = 0.1;
var mouseText;

var nodeSelection;
var nodeTexts;
var edgeTexts;

window.onload = function () {
    if (!Detector.webgl)
        Detector.addGetWebGLMessage();

    container3D = document.getElementById('Container3D');

    var width = 1024;
    var height = 1024;

    scene = new THREE.Scene();
    scene.background = new THREE.Color(0xEEEEEE);
    camera = new THREE.PerspectiveCamera(25, width / height, 0.1, 1000);

    renderer = new THREE.WebGLRenderer({antialias: true});
    renderer.setSize(width, height);
    renderer.setPixelRatio(window.devicePixelRatio);

    // LIGHTS
    ambientLight = new THREE.AmbientLight(0x333333);
    scene.add(ambientLight);
    directionalLight = new THREE.DirectionalLight(0xAAAAAA, 1.0);
    scene.add(directionalLight);

    //CONTROLS
    controls = new THREE.OrbitControls(camera, renderer.domElement);
    controls.addEventListener('change', render); // use only if there is no animation loop
    controls.enablePan = false;

    //INTERSECTION RAYCASTER
    raycaster = new THREE.Raycaster();
   
    //mouse coordinates
    mouse = new THREE.Vector2();
    container3D.addEventListener('mousemove', onDocumentMouseMove, false);
    container3D.addEventListener('click', onDocumentMouseClick, false);

    //mouse text
    mouseText = document.createElement('div');
    mouseText.style.position = 'absolute';
    //mouseText.style.zIndex = 1;    // if you still don't see the label, try uncommenting this
    mouseText.style.width = 200;
    mouseText.style.height = 400;
    mouseText.style.backgroundColor = "#ccb3ff";
    mouseText.style.paddingTop = 20 + 'px';
    mouseText.style.paddingBottom = 20 + 'px';
    mouseText.style.paddingRight = 20 + 'px';
    mouseText.style.paddingLeft = 20 + 'px';
    mouseText.innerHTML = "Node";
    mouseText.style.top = 0 + 'px';
    mouseText.style.left = 0 + 'px';
    mouseText.style.visibility='hidden';
    document.body.appendChild(mouseText);


    container3D.appendChild(renderer.domElement);
    render();
};

function onDocumentMouseClick(event) {
    if(currentIntersectedPointIndex < 0)
        return;
    
    if(nodeSelection[currentIntersectedPointIndex] === 0)
        nodeSelection[currentIntersectedPointIndex] = 1;
    else
        nodeSelection[currentIntersectedPointIndex] = 0;
    
    draw3D();
}


function onDocumentMouseMove(event) {
    event.preventDefault();

    let canvasBounds = renderer.context.canvas.getBoundingClientRect();
    mouse.x = ( ( event.clientX - canvasBounds.left ) / ( canvasBounds.right - canvasBounds.left ) ) * 2 - 1;
    mouse.y = - ( ( event.clientY - canvasBounds.top ) / ( canvasBounds.bottom - canvasBounds.top) ) * 2 + 1;

//    var mouseOver = false;
//    if(mouse.x >= -1 && mouse.x <= 1 && mouse.y >= -1 && mouse.y <= 1) //this doesn't seem precise as we are in the range grater than -1:1 ?? 
//        mouseOver = true;    
//    if(mouseOver)
//        mouseText.style.visibility='visible';
//    else
//        mouseText.style.visibility='hidden';
          
    mouseText.style.left = 20 + event.clientX + 'px';
    mouseText.style.top = event.clientY + 'px';
    
    var mousePos = document.getElementById("mouse_pos");
    mousePos.innerHTML = mouse.x+", "+mouse.y;
    
    raycaster.setFromCamera(mouse, camera); 

    var ok = false;
    if(pointsObject !== undefined) {
        // find intersections with points
        var intersects = raycaster.intersectObject(pointsObject);
        var pp;
        var imin;

        //nie koniecznie [0], pętla po wszystkich i min odległości
        if(intersects.length > 0) {
            //distance
            var geometry = pointsObject.geometry;
            var attributes = geometry.attributes;
            var coords = attributes.position.array;
            var minD = 2*pointSize;
            //TBD check only N=?10 first points sorted by distance from camera
            for (var i = 0; i < intersects.length; i++) {
                var ip = intersects[i].point;
                var p = new THREE.Vector3(coords[3*intersects[i].index],coords[3*intersects[i].index+1],coords[3*intersects[i].index+2]);
                var d = ip.distanceTo(p);
                if(d < minD) {
                    minD = d;
                    imin = i;
                    pp = p;
                }
            }
            if(minD < pointSize/4)
                ok = true; 
        } 
        if (ok) {
            if (currentIntersectedLine !== undefined) {
                currentIntersectedLine.material.linewidth = 1;
            }
            currentIntersectedLine = undefined;
            currentIntersectedLineIndex = -1;

            currentIntersectedPoint = intersects[imin].object;
            currentIntersectedPointIndex = intersects[imin].index;
            hooverPointSphere.position.copy(pp);
            hooverPointSphere.visible = true;
            
            if(nodeTexts !== undefined && currentIntersectedPointIndex >= 0 && currentIntersectedPointIndex < nodeTexts.length)
                mouseText.innerHTML=""+currentIntersectedPointIndex+": "+nodeTexts[currentIntersectedPointIndex];
            else 
                mouseText.innerHTML="no data";
            
            mouseText.style.visibility='visible';
        } else {
            currentIntersectedPoint = undefined;
            currentIntersectedPointIndex = -1;
            hooverPointSphere.visible = false;
            mouseText.style.visibility='hidden';
        }       
    }
    
    // find intersections with lines
    if(!ok && lineObjects !== undefined) {
        intersects = raycaster.intersectObjects(lineObjects); 
        if (intersects.length > 0) {
            if (currentIntersectedLine !== undefined) {
                currentIntersectedLine.material.linewidth = 1;
                currentIntersectedLineIndex = -1;
            }
            currentIntersectedLine = intersects[ 0 ].object;
            for (var i = 0; i < lineObjects.length; i++) {
                if(lineObjects[i] === currentIntersectedLine) {
                    currentIntersectedLineIndex = i;
                    break;
                } 
            }
            
            currentIntersectedLine.material.linewidth = 10;
            if(edgeTexts !== undefined && currentIntersectedLineIndex >= 0 && currentIntersectedLineIndex < edgeTexts.length)
                mouseText.innerHTML=""+currentIntersectedLineIndex+": "+edgeTexts[currentIntersectedLineIndex];
            else 
                mouseText.innerHTML="no data";
            
            mouseText.style.visibility='visible';
        } else {
            if (currentIntersectedLine !== undefined) {
                currentIntersectedLine.material.linewidth = 1;
            }
            currentIntersectedLine = undefined;
            mouseText.style.visibility='hidden';
        } 
    }
    
    render();
}

function draw3D() {
    if(pointsObject !== undefined)
        scene.remove(pointsObject);
    if(lineObjects !== undefined) {
        for (var i = 0; i < lineObjects.length; i++)
            scene.remove(lineObjects[i]);
        lineObjects = [];
    }
    if(hooverPointSphere !== undefined)
        scene.remove(hooverPointSphere);
    if(pointSpheres !== undefined) {
        for (var i = 0; i < pointSpheres.length; i++)
            scene.remove(pointSpheres[i]);
        pointSpheres = [];        
    }

    //get object data from scope
    var coords = dataobject.coords;
    var segments = dataobject.segments;
    var nNodes = coords.length/3;


    //create nodes geometry
    var pointsGeometry = new THREE.BufferGeometry();
    pointsGeometry.addAttribute( 'position', new THREE.Float32BufferAttribute( coords, 3 ) );
    
    //get scene geometric extents
    pointsGeometry.computeBoundingSphere();
    pointsGeometry.computeBoundingBox();
    var bb = pointsGeometry.boundingBox;
    var bbcenter = bb.getCenter();
    var bbsize = bb.getSize();
    //set camera position to view the whole scene
    var maxDim = Math.max(bbsize.x, bbsize.y, bbsize.z);
    var fov = camera.fov * (Math.PI / 180);
    var cameraZ = Math.abs(maxDim / (2 * Math.tan(fov / 2))) * 2.0;
    camera.position.set(bbcenter.x, bbcenter.y, cameraZ);
    
    //create points at nodes
    pointSize = 0.1 * maxDim;
    var pointsMaterial = new THREE.PointsMaterial({color: 0x0000ff, size: pointSize});
    pointsObject = new THREE.Points(pointsGeometry, pointsMaterial);
    scene.add(pointsObject);
    raycaster.linePrecision = pointSize/8;

    //create sphere for point hoover
    var sphereGeometry = new THREE.SphereGeometry( pointSize/4, 32, 32 );
	var sphereMaterial = new THREE.MeshBasicMaterial( { color: 0xff0000 } );
    hooverPointSphere = new THREE.Mesh( sphereGeometry, sphereMaterial );
    hooverPointSphere.visible = false;
    scene.add( hooverPointSphere );
    
    //create spheres for point selection
    for (var i = 0; i < nNodes; i++) {
        if(nodeSelection[i] === 0)
            continue;
        
        var sphereGeometry = new THREE.SphereGeometry( pointSize/4, 32, 32 );
        var sphereMaterial = new THREE.MeshBasicMaterial( { color: 0x00ff00 } );
        pointSpheres[i] = new THREE.Mesh( sphereGeometry, sphereMaterial );
        pointSpheres[i].visible = true;
        pointSpheres[i].position.x = coords[3 * i];
        pointSpheres[i].position.y = coords[3 * i + 1];
        pointSpheres[i].position.z = coords[3 * i + 2];
        scene.add( pointSpheres[i] );
    }

    //create segments geometry and lines
    var nSegments = segments.length / 2;
    var a, b;
    for (var i = 0; i < nSegments; i++) {
        var linesGeometry = new THREE.Geometry();
        a = segments[2 * i];
        b = segments[2 * i + 1];
        linesGeometry.vertices.push(new THREE.Vector3(coords[3 * a], coords[3 * a + 1], coords[3 * a + 2]));
        linesGeometry.vertices.push(new THREE.Vector3(coords[3 * b], coords[3 * b + 1], coords[3 * b + 2]));
        var linesMaterial = new THREE.LineBasicMaterial({color: 0xff0000, linewidth: 1});
        var lineObject = new THREE.LineSegments(linesGeometry, linesMaterial);
        lineObjects.push(lineObject);
        scene.add(lineObject);
    }

    render();
}

function render() {
    //set light position at camera position
    directionalLight.position.set(camera.position.x, camera.position.y, camera.position.z).normalize();
    //do rendering
    renderer.render(scene, camera);
}

app.controller('RetrieveMyObjectController', ['$scope', '$http', '$q', function ($scope, $http, $q) {
        $scope.retrieveMyObject = function () {
            var ID = $scope.dataobject.id;
            var text = $scope.dataobject.text;

            $http.get('http://localhost:8080/dataobject?id=' + ID + '&text=' + text).
                    then(function (response) {
                        $scope.dataobject = response.data;
                        if ($scope.dataobject.text === "undefined")
                            $scope.dataobject.text = "";

                        var showID = document.getElementById("id_display");
                        showID.innerHTML = $scope.dataobject.id;

                        var showSEQ = document.getElementById("seq_display");
                        showSEQ.innerHTML = ";  Text: " + $scope.dataobject.text;
                        
                        dataobject = $scope.dataobject;
                        
                        nodeTexts = dataobject.nodeData;
                        edgeTexts = dataobject.segmentData;
                        nodeSelection = new Uint8Array(nodeTexts.length);
                        for (var i = 0; i < nodeTexts.length; i++) {
                            nodeSelection[i] = 0;
                        }

                        draw3D();
                        render();

                    });
        };

    }]);







