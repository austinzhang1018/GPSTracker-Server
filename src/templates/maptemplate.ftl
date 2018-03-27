<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
    <title>${name}'s location</title>
    <style>
        /* Always set the map height explicitly to define the size of the div
         * element that contains the map. */
        #map {
            height: 100%;
        }
        /* Optional: Makes the sample page fill the window. */
        html, body {
            height: 100%;
            margin: 0;
            padding: 0;
        }
    </style>
</head>
<body>
<div id="map"></div>
<script>

    function initMap() {
        var map = new google.maps.Map(document.getElementById('map'), {
            zoom: 11,
            center: {lat: 40.071058, lng: -82.920480},
            mapTypeId: 'roadmap'
        });

        var trackerPathCoordinates = [
            ${coordinates}
        ];
        var trackerPath= new google.maps.Polyline({
            path: trackerPathCoordinates,
            geodesic: true,
            strokeColor: '#FF0000',
            strokeOpacity: 1.0,
            strokeWeight: 2
        });

        var accuracyCircle = new google.maps.Circle({
            strokeColor: '#FF0000',
            strokeOpacity: 0.8,
            strokeWeight: 2,
            fillColor: '#FF0000',
            fillOpacity: 0.35,
            map: map,
            center: ${lastPosition},
            radius: ${accuracy}
        });

        addMarker(${lastPosition}, map);

        // Adds a marker to the map.
        function addMarker(location, map) {
            // Add the marker at the clicked location, and add the next-available label
            // from the array of alphabetical characters.
            var marker = new google.maps.Marker({
                position: location,
                label: '${name}',
                map: map
            });
        }

        trackerPath.setMap(map);
    }
</script>
<script async defer
        src="https://maps.googleapis.com/maps/api/js?key=AIzaSyC3bMKKuZRqCYNeY0ENRXO4PVnYFEtOxbM&callback=initMap">
</script>
</body>
</html>