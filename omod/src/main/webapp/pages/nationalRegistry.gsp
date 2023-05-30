<%
    // although called "patientDashboard" this is actually the patient visits screen, and clinicianfacing/patient is the main patient dashboard
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("uicommons", "bootstrap-collapse.js")
    ui.includeJavascript("uicommons", "bootstrap-transition.js")
    ui.includeCss("uicommons", "styleguide/index.styles")
    ui.includeCss("uicommons", "datatables/dataTables_jui.styles")
    ui.includeJavascript("ugandaemrsync", "synctasktype.js")

    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("National Facility Registry") ])
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("coreapps.app.systemAdministration.label")}", link: '/' + OPENMRS_CONTEXT_PATH + '/coreapps/systemadministration/systemAdministration.page'},
        { label: "UgandaEMR Sync", link: '/' + OPENMRS_CONTEXT_PATH + '/ugandaemrsync/ugandaemrsync.page'},
        { label: "Search National Facility Registry"}
    ];

</script>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("coreapps.app.systemAdministration.label")}", link: '/' + OPENMRS_CONTEXT_PATH + '/coreapps/systemadministration/systemAdministration.page'},
        { label: "UgandaEMR Sync", link: '/' + OPENMRS_CONTEXT_PATH + '/ugandaemrsync/ugandaemrsync.page'},
        { label: "Search National Facility Registry"}
    ];

</script>

<script type="text/javascript">
    if(jQuery){
        jq(document).ready(function (){

            var payload = {
                "message": "success",
                "status": true,
                "data": {
                    "resourceType": "Bundle",
                    "id": "3da9028a-900f-436b-89ff-49f23adc6b75",
                    "meta": {
                        "lastUpdated": "2023-05-19T08:42:10.463+00:00"
                    },
                    "type": "searchset",
                    "total": 17,
                    "link": [
                        {
                            "relation": "self",
                            "url": "http://154.72.198.135:8082/fhir/Location?_count=2000&status=active&type=Region"
                        }
                    ],
                    "entry": [
                        {
                            "fullUrl": "http://154.72.198.135:8082/fhir/Location/90",
                            "resource": {
                                "resourceType": "Location",
                                "id": "90",
                                "meta": {
                                    "versionId": "1",
                                    "lastUpdated": "2022-04-06T10:06:05.680+00:00",
                                    "source": "#xtiEbhQ9uv3ErqSk"
                                },
                                "extension": [
                                    {
                                        "url": "code",
                                        "valueString": "UNKNOWN"
                                    }
                                ],
                                "status": "active",
                                "name": "UNKNOWN",
                                "type": [
                                    {
                                        "coding": [
                                            {
                                                "code": "Region"
                                            }
                                        ]
                                    }
                                ]
                            },
                            "search": {
                                "mode": "match"
                            }
                        },
                        {
                            "fullUrl": "http://154.72.198.135:8082/fhir/Location/95",
                            "resource": {
                                "resourceType": "Location",
                                "id": "95",
                                "meta": {
                                    "versionId": "1",
                                    "lastUpdated": "2022-04-06T13:40:59.238+00:00",
                                    "source": "#lGJeHlgxkw1vQw4S"
                                },
                                "extension": [
                                    {
                                        "url": "code",
                                        "valueString": "Ankole"
                                    }
                                ],
                                "status": "active",
                                "name": "Ankole",
                                "type": [
                                    {
                                        "coding": [
                                            {
                                                "code": "Region"
                                            }
                                        ]
                                    }
                                ]
                            },
                            "search": {
                                "mode": "match"
                            }
                        },
                        {
                            "fullUrl": "http://154.72.198.135:8082/fhir/Location/96",
                            "resource": {
                                "resourceType": "Location",
                                "id": "96",
                                "meta": {
                                    "versionId": "1",
                                    "lastUpdated": "2022-04-06T13:40:59.294+00:00",
                                    "source": "#wMZ5OitVzj4hNqC3"
                                },
                                "extension": [
                                    {
                                        "url": "code",
                                        "valueString": "Teso"
                                    }
                                ],
                                "status": "active",
                                "name": "Teso",
                                "type": [
                                    {
                                        "coding": [
                                            {
                                                "code": "Region"
                                            }
                                        ]
                                    }
                                ]
                            },
                            "search": {
                                "mode": "match"
                            }
                        },
                        {
                            "fullUrl": "http://154.72.198.135:8082/fhir/Location/97",
                            "resource": {
                                "resourceType": "Location",
                                "id": "97",
                                "meta": {
                                    "versionId": "1",
                                    "lastUpdated": "2022-04-06T13:40:59.357+00:00",
                                    "source": "#DxjgmRHFidigyOXu"
                                },
                                "extension": [
                                    {
                                        "url": "code",
                                        "valueString": "North Central"
                                    }
                                ],
                                "status": "active",
                                "name": "North Central",
                                "type": [
                                    {
                                        "coding": [
                                            {
                                                "code": "Region"
                                            }
                                        ]
                                    }
                                ]
                            },
                            "search": {
                                "mode": "match"
                            }
                        },
                        {
                            "fullUrl": "http://154.72.198.135:8082/fhir/Location/98",
                            "resource": {
                                "resourceType": "Location",
                                "id": "98",
                                "meta": {
                                    "versionId": "1",
                                    "lastUpdated": "2022-04-06T13:40:59.416+00:00",
                                    "source": "#ruzJ6SB5GL2g7vhT"
                                },
                                "extension": [
                                    {
                                        "url": "code",
                                        "valueString": "Kigezi"
                                    }
                                ],
                                "status": "active",
                                "name": "Kigezi",
                                "type": [
                                    {
                                        "coding": [
                                            {
                                                "code": "Region"
                                            }
                                        ]
                                    }
                                ]
                            },
                            "search": {
                                "mode": "match"
                            }
                        },
                        {
                            "fullUrl": "http://154.72.198.135:8082/fhir/Location/99",
                            "resource": {
                                "resourceType": "Location",
                                "id": "99",
                                "meta": {
                                    "versionId": "1",
                                    "lastUpdated": "2022-04-06T13:40:59.469+00:00",
                                    "source": "#O8lQerdZrRdzJb5A"
                                },
                                "extension": [
                                    {
                                        "url": "code",
                                        "valueString": "Tooro"
                                    }
                                ],
                                "status": "active",
                                "name": "Tooro",
                                "type": [
                                    {
                                        "coding": [
                                            {
                                                "code": "Region"
                                            }
                                        ]
                                    }
                                ]
                            },
                            "search": {
                                "mode": "match"
                            }
                        },
                        {
                            "fullUrl": "http://154.72.198.135:8082/fhir/Location/100",
                            "resource": {
                                "resourceType": "Location",
                                "id": "100",
                                "meta": {
                                    "versionId": "1",
                                    "lastUpdated": "2022-04-06T13:40:59.511+00:00",
                                    "source": "#h87NanlqfB6Sk1gy"
                                },
                                "extension": [
                                    {
                                        "url": "code",
                                        "valueString": "West Nile"
                                    }
                                ],
                                "status": "active",
                                "name": "West Nile",
                                "type": [
                                    {
                                        "coding": [
                                            {
                                                "code": "Region"
                                            }
                                        ]
                                    }
                                ]
                            },
                            "search": {
                                "mode": "match"
                            }
                        },
                        {
                            "fullUrl": "http://154.72.198.135:8082/fhir/Location/101",
                            "resource": {
                                "resourceType": "Location",
                                "id": "101",
                                "meta": {
                                    "versionId": "1",
                                    "lastUpdated": "2022-04-06T13:40:59.552+00:00",
                                    "source": "#diOsgxpKyJEuS2zy"
                                },
                                "extension": [
                                    {
                                        "url": "code",
                                        "valueString": "South Central"
                                    }
                                ],
                                "status": "active",
                                "name": "South Central",
                                "type": [
                                    {
                                        "coding": [
                                            {
                                                "code": "Region"
                                            }
                                        ]
                                    }
                                ]
                            },
                            "search": {
                                "mode": "match"
                            }
                        },
                        {
                            "fullUrl": "http://154.72.198.135:8082/fhir/Location/102",
                            "resource": {
                                "resourceType": "Location",
                                "id": "102",
                                "meta": {
                                    "versionId": "1",
                                    "lastUpdated": "2022-04-06T13:40:59.583+00:00",
                                    "source": "#MjrM9B5iNkhGhHkf"
                                },
                                "extension": [
                                    {
                                        "url": "code",
                                        "valueString": "Bunyoro"
                                    }
                                ],
                                "status": "active",
                                "name": "Bunyoro",
                                "type": [
                                    {
                                        "coding": [
                                            {
                                                "code": "Region"
                                            }
                                        ]
                                    }
                                ]
                            },
                            "search": {
                                "mode": "match"
                            }
                        },
                        {
                            "fullUrl": "http://154.72.198.135:8082/fhir/Location/103",
                            "resource": {
                                "resourceType": "Location",
                                "id": "103",
                                "meta": {
                                    "versionId": "1",
                                    "lastUpdated": "2022-04-06T13:40:59.615+00:00",
                                    "source": "#SjOcLhiyUun5cGXH"
                                },
                                "extension": [
                                    {
                                        "url": "code",
                                        "valueString": "Acholi"
                                    }
                                ],
                                "status": "active",
                                "name": "Acholi",
                                "type": [
                                    {
                                        "coding": [
                                            {
                                                "code": "Region"
                                            }
                                        ]
                                    }
                                ]
                            },
                            "search": {
                                "mode": "match"
                            }
                        },
                        {
                            "fullUrl": "http://154.72.198.135:8082/fhir/Location/104",
                            "resource": {
                                "resourceType": "Location",
                                "id": "104",
                                "meta": {
                                    "versionId": "1",
                                    "lastUpdated": "2022-04-06T13:40:59.652+00:00",
                                    "source": "#KgkJ0uXerWOylWlf"
                                },
                                "extension": [
                                    {
                                        "url": "code",
                                        "valueString": "Lango"
                                    }
                                ],
                                "status": "active",
                                "name": "Lango",
                                "type": [
                                    {
                                        "coding": [
                                            {
                                                "code": "Region"
                                            }
                                        ]
                                    }
                                ]
                            },
                            "search": {
                                "mode": "match"
                            }
                        },
                        {
                            "fullUrl": "http://154.72.198.135:8082/fhir/Location/105",
                            "resource": {
                                "resourceType": "Location",
                                "id": "105",
                                "meta": {
                                    "versionId": "1",
                                    "lastUpdated": "2022-04-06T13:40:59.682+00:00",
                                    "source": "#Fda8TwX0dlsxWtwN"
                                },
                                "extension": [
                                    {
                                        "url": "code",
                                        "valueString": "Busoga"
                                    }
                                ],
                                "status": "active",
                                "name": "Busoga",
                                "type": [
                                    {
                                        "coding": [
                                            {
                                                "code": "Region"
                                            }
                                        ]
                                    }
                                ]
                            },
                            "search": {
                                "mode": "match"
                            }
                        },
                        {
                            "fullUrl": "http://154.72.198.135:8082/fhir/Location/106",
                            "resource": {
                                "resourceType": "Location",
                                "id": "106",
                                "meta": {
                                    "versionId": "1",
                                    "lastUpdated": "2022-04-06T13:40:59.707+00:00",
                                    "source": "#J8vTSwRVQLDcmEeq"
                                },
                                "extension": [
                                    {
                                        "url": "code",
                                        "valueString": "Karamoja"
                                    }
                                ],
                                "status": "active",
                                "name": "Karamoja",
                                "type": [
                                    {
                                        "coding": [
                                            {
                                                "code": "Region"
                                            }
                                        ]
                                    }
                                ]
                            },
                            "search": {
                                "mode": "match"
                            }
                        },
                        {
                            "fullUrl": "http://154.72.198.135:8082/fhir/Location/107",
                            "resource": {
                                "resourceType": "Location",
                                "id": "107",
                                "meta": {
                                    "versionId": "1",
                                    "lastUpdated": "2022-04-06T13:40:59.742+00:00",
                                    "source": "#7fOoHHZP1TAojrhR"
                                },
                                "extension": [
                                    {
                                        "url": "code",
                                        "valueString": "Bugisu"
                                    }
                                ],
                                "status": "active",
                                "name": "Bugisu",
                                "type": [
                                    {
                                        "coding": [
                                            {
                                                "code": "Region"
                                            }
                                        ]
                                    }
                                ]
                            },
                            "search": {
                                "mode": "match"
                            }
                        },
                        {
                            "fullUrl": "http://154.72.198.135:8082/fhir/Location/108",
                            "resource": {
                                "resourceType": "Location",
                                "id": "108",
                                "meta": {
                                    "versionId": "1",
                                    "lastUpdated": "2022-04-06T13:40:59.772+00:00",
                                    "source": "#hn0MR7u9gtOx9dVj"
                                },
                                "extension": [
                                    {
                                        "url": "code",
                                        "valueString": "Bukedi"
                                    }
                                ],
                                "status": "active",
                                "name": "Bukedi",
                                "type": [
                                    {
                                        "coding": [
                                            {
                                                "code": "Region"
                                            }
                                        ]
                                    }
                                ]
                            },
                            "search": {
                                "mode": "match"
                            }
                        },
                        {
                            "fullUrl": "http://154.72.198.135:8082/fhir/Location/109",
                            "resource": {
                                "resourceType": "Location",
                                "id": "109",
                                "meta": {
                                    "versionId": "1",
                                    "lastUpdated": "2022-04-06T13:40:59.808+00:00",
                                    "source": "#vgiNDpC63QM0nAbH"
                                },
                                "extension": [
                                    {
                                        "url": "code",
                                        "valueString": "Kampala"
                                    }
                                ],
                                "status": "active",
                                "name": "Kampala",
                                "type": [
                                    {
                                        "coding": [
                                            {
                                                "code": "Region"
                                            }
                                        ]
                                    }
                                ]
                            },
                            "search": {
                                "mode": "match"
                            }
                        },
                        {
                            "fullUrl": "http://154.72.198.135:8082/fhir/Location/75005",
                            "resource": {
                                "resourceType": "Location",
                                "id": "75005",
                                "meta": {
                                    "versionId": "1",
                                    "lastUpdated": "2022-04-27T11:25:01.335+00:00",
                                    "source": "#IpNaNk7hHhi86cVX"
                                },
                                "extension": [
                                    {
                                        "url": "code",
                                        "valueString": "555"
                                    }
                                ],
                                "status": "active",
                                "name": "Test Region",
                                "type": [
                                    {
                                        "coding": [
                                            {
                                                "code": "Region"
                                            }
                                        ]
                                    }
                                ]
                            },
                            "search": {
                                "mode": "match"
                            }
                        }
                    ]
                }
            };
            var list = payload.data.entry;
            var sel = document.getElementById('region');
            if(list){
                for(var i = 0; i < list.length; i++) {
                    var opt = document.createElement('option');
                    opt.innerHTML = list[i].resource.name;
                    opt.value = list[i].resource.id;
                    sel.appendChild(opt);
                }
            }

            jq("#region").change(function(){
                var option = jq('#region').val();
                getDistricts(option);
            })

            jq("#submit").click(function(){
                var regionID = jq('#region').val();
                var districtID = jq('#district').val();
                getHealthFacilities(regionID, districtID);
            })

        });

        function getDistricts(regionId){
            jq.ajax({
                type: "GET",
                url: "https://nhfr-staging-api.planetsystems.co/nhfrApi/v0.0.1/externalSystem/byParentId/"+regionId,
                dataType: "json",
                contentType: "application/json",
                async: false,
                success: function (data) {
                    var districts = data.data.entry;
                    jq('#district').find('option:not(:first)').remove();
                    for (var i = 0; i<districts.length; i++) {
                        jq('#district').append("<option value='"+ districts[i].resource.id+"'>"+ districts[i].resource.name+ "</option>");
                    }
                }
            });
        }

        function getHealthFacilities(regionID, districtID){
            jq.ajax({
                type: "GET",
                url: "https://nhfr-staging-api.planetsystems.co/nhfrApi/v0.0.1/externalSystem/search?count=40&region="+ regionID+ "&localGovernment="+ districtID,
                dataType: "json",
                contentType: "application/json",
                async: false,
                success: function(data){
                    var tableRow = "";
                    var healthFacilities = data.data.entry;
                    for (var i = 0; i < healthFacilities.length; i++) {
                        var id = healthFacilities[i].resource.id;
                        var name = healthFacilities[i].resource.name;
                        var subCounty = healthFacilities[i].resource.partOf.display;
                        var row = "<tr><td>" + id + "</td><td>" + name + "</td><td>" + subCounty + "</td>/tr>";
                        tableRow += row;
                    }
                    jq('#body').empty();
                    jq('#region').val('');
                    jq('#district').val('');
                    jq('#body').append(tableRow);
                }
            });
        }
    }
</script>
<div class="row">
    <div class="col-md-12">
        <div class="card">
            <div class="card-header">
                Search National Facility Registry
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-3">
                        <div class="form-group">
                            <label>Region</label>
                            <select class="form-control" name="region" id="region">
                                <option value="">Select Region</option>
                            </select>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <label>District:</label>
                        <select class="form-control" name="district" id="district">
                            <option value="">Select District</option>
                        </select>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-3">
                        <input type="submit" id="submit">
                    </div>
                </div>
            </div>
        </div>

        <div class="card">
            <div class="card-header">
                Facilities Found
            </div>
            <div class="card-body">
                <table>
                    <thead>
                    <tr>
                        <th>Health Facility ID</th>
                        <th>Name</th>
                        <th>SubCounty</th>
                        <th>View Facility</th>
                    </tr>
                    </thead>
                    <tbody id="body">

                    </tbody>
                </table>
            </div>
        </div>

    </div>

</div>