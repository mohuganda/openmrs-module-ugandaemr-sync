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
                            <option value="central">Central</option>
                            <option value="western">Western</option>
                            <option value="eastern">Eastern</option>
                            <option value="northern">Northern</option>
                        </select>
                    </div>
                </div>
                <div class="col-md-3">
                    <label>District:</label>
                    <select class="form-control" name="district" id="district">
                        <option value="">Select District</option>
                        <option value="mukono">Mukono</option>
                        <option value="wakiso">Wakiso</option>
                        <option value="kampala">Kampala</option>
                        <option value="busia">Busia</option>
                    </select>
                </div>
            </div>
            <div class="row">
                <div class="col-md-3">
                    <input type="submit">
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
                    <th>Name</th>
                    <th>Description</th>
                    <th>Group Type</th>
                    <th>Created On</th>
                    <th>View Facility</th>
                </tr>
                </thead>
                <tbody>

                </tbody>
            </table>
        </div>
    </div>

</div>

</div>
