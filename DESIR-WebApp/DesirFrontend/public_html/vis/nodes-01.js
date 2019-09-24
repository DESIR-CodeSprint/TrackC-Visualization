
  var pointGeo = new THREE.Geometry();
  var image = document.createElement('img');
  var texture = new THREE.Texture('image');

  var mat = new THREE.PointsMaterial({
    //vertexColors: true,
    size: 5, 
    color: 0xffffff,
    map: new THREE.TextureLoader().load('img/xs-stars.png'), // '2.jpg' 
    transparent: true,
    side: THREE.DoubleSide,
    depthTest: false
  });

  var points = new THREE.Points(pointGeo, mat);

  function loadStars(data){
    scene.remove(points);
  	//pointGeo.dispose();
    pointGeo.vertices = [];
    var pointCount = data.length;
      for (var i = 0; i < pointCount; i ++) {
        var x = scale(data[i].x);
        var y = scale(data[i].y);
        var z = scale(data[i].z);
        pointGeo.vertices.push(new THREE.Vector3(x, y, z));
      }
    scene.add(points);
    render();
  }


