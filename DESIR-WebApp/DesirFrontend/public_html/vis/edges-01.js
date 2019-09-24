var container, camera, controls, scene, renderer;

var scale = d3.scale.linear()
        .domain([0, 75000])
        .range([-50, 50]);


camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 1, 500);
camera.position.z = 100;
// camera.position.set(0, 0, 200);
// camera.lookAt(new THREE.Vector3(0, 0, 0));

renderer = new THREE.WebGLRenderer({antialias: true});
renderer.setSize(window.innerWidth, window.innerHeight);
renderer.autoClear = true;

controls = new THREE.TrackballControls(camera, renderer.domElement);

controls.rotateSpeed = 2.0;
controls.zoomSpeed = 1.2;
controls.panSpeed = 0.8;

controls.noZoom = false;
controls.noPan = false;

controls.staticMoving = true;
controls.dynamicDampingFactor = 0.3;

controls.minDistance = 0;
controls.maxDistance = 200;

controls.keys = [65, 83, 68];

controls.addEventListener('change', render);

scene = new THREE.Scene();
scene.fog = new THREE.FogExp2(0x000, 0.003);

renderer.setClearColor(scene.fog.color);


var geometry = new THREE.Geometry();

var material = new THREE.LineBasicMaterial({
    color: 0x999999,
    opacity: 0.2,
    blending: THREE.AdditiveBlending,
    transparent: true
});

var line = new THREE.LineSegments(geometry, material);

function nodes() {

    var infos = document.querySelector('input[name="edgeSelect"]:checked').value;

    var pushHistory = "#" + document.querySelector('input[name="edgeSelect"]:checked').id.substr(5);

    history.pushState({}, '', pushHistory);

    if (pushHistory === '#1') {
        document.getElementById("fixed_length").style.display = 'block';
        document.getElementById("varying_length").style.display = 'none';
        document.getElementById("nearest_neighbour").style.display = 'none';
    } else if (pushHistory === '#2') {
        document.getElementById("fixed_length").style.display = 'none';
        document.getElementById("varying_length").style.display = 'block';
        document.getElementById("nearest_neighbour").style.display = 'none';
    } else if (pushHistory === '#3') {
        document.getElementById("fixed_length").style.display = 'none';
        document.getElementById("varying_length").style.display = 'none';
        document.getElementById("nearest_neighbour").style.display = 'block';
    } else {
        document.getElementById("fixed_length").style.display = 'none';
        document.getElementById("varying_length").style.display = 'none';
        document.getElementById("nearest_neighbour").style.display = 'none';
    }

    d3.csv("data/ccnr-universe-" + infos + ".csv", function (data) {
        edges = data;
        filter(pushHistory);
        return edges;
    });
    
}

function stars(nodes) {

    scene.remove(line);
    //geometry.dispose();

    geometry.vertices = [];

    var i = edges.length;

    while (i--) {

        var edge = edges[i];

        if (nodes[edge.source] !== "" && nodes[edge.target] !== "") {

            geometry.vertices.push(
                    new THREE.Vector3(
                            scale(nodes[edge.source].x),
                            scale(nodes[edge.source].y),
                            scale(nodes[edge.source].z)),
                    new THREE.Vector3(
                            scale(nodes[edge.target].x),
                            scale(nodes[edge.target].y),
                            scale(nodes[edge.target].z))

                    );

        }

    }

    line = new THREE.LineSegments(geometry, material);
    scene.add(line);
    render();
}

nodes();

container = document.getElementById('network');
document.body.appendChild(container);
container.appendChild(renderer.domElement);

window.addEventListener('resize', onWindowResize, false);

function onWindowResize() {

    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();

    renderer.setSize(window.innerWidth, window.innerHeight);

    controls.handleResize();

    render();

}

function animate() {

    requestAnimationFrame(animate);
    controls.update();

}

function render() {

    controls.handleResize();
    renderer.render(scene, camera);

}

animate();



