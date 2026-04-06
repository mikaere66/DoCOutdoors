# DoC Outdoors

Written in Jetpack Compose, the DoC Outdoors app is intended to provide simple and straightforward listings of the various New Zealand Department of Conservation (DoC) assets that are maintained for public leisure.

As well as Campsites, Huts, and Tracks, there's a list of Alerts, which highlights any current closures and/or hazards on DoC sites. These alerts can be updated manually or automatically: either daily or weekly, using the Work Manager API.

All data is sourced from DoC servers in the .json format, and processed/then stored within the app using Kotlin Serialization and Room database APIs.

For this app to function **fully**, you will need to provide **two** API keys: one from [Department of Conservation](https://api.doc.govt.nz/getting-started), and the other from [Google Maps](https://developers.google.com/maps/get-started)