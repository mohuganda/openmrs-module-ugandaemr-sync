<%
    ui.decorateWith("appui", "standardEmrPage", [title: ui.message("Send Reports Page")])
    ui.includeJavascript("uicommons", "bootstrap-collapse.js")
    ui.includeJavascript("uicommons", "bootstrap-transition.js")

    def htmlSafeId = { extension ->
        "${extension.id.replace(".", "-")}-${extension.id.replace(".", "-")}-extension"
    }
    def breadcrumbMiddle = breadcrumbOverride ?: '';
%>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("coreapps.app.systemAdministration.label")}", link: '/' + OPENMRS_CONTEXT_PATH + '/coreapps/systemadministration/systemAdministration.page'},
        { label: "UgandaEMR Sync", link: '/' + OPENMRS_CONTEXT_PATH + '/ugandaemrsync/ugandaemrsync.page'},
        { label: "Send Reports"}
    ];

    jq(document).ready(function () {

        jq('#upload_vl').submit(function(){
            if(!jq('input[type="file"]').val()) {
                jq().toastmessage('showNoticeToast', "No file was uploaded");
                return false;
            }else{
                return true;
            }
        });

        var errorMessage = jq('#errorMessage').val();
        if(errorMessage!==""){
            jq().toastmessage('showNoticeToast', errorMessage);
        }


    });
</script>
<style>
#browser_file_container {
    width: 80%;
    padding-left: 0px;
    padding-right: 0px;
}

#upload_button_container {
    width: 20%;
    text-align: right;
    padding-left: 0px;
    padding-right: 0px;
}

#browser_file {
    width: 109%;
    text-align: right;
}

.div-col3 {
    padding-left: 30px;
    padding-right: 30px;
}

.div-col2 {
    padding-left: 30px;
    padding-right: 30px;
}
</style>

<div>
    <label style="text-align: center"><h1>Send EMR Reports to DHIS2 </h1></label>

</div>

<form method="post" id="sendReports">
    <div class="div-row">
        <div class="div-col8">
            <select id="reportDefinitions" name="reportDefinition" class="col-lg-5">
                <% if (reportDefinitions.size() > 0) {
                    reportDefinitions.each { %>
                      <option value="${it.uuid}">${it.name}</option>
                    <% }
                    } %>
            </select>
        </div>
        <div class="div-col4">
        <legend> Period Parameter</legend>
        <div>
            <label>Start Date</label>
            <input type="date" name="startDate" id="startDate">
            <p></p>
            <label>End Date</label>
            <input type="date" name="endDate" id="endDate">
        </div>
          <button type="submit" class="confirm right" ng-class="{disabled: submitting}" ng-disabled="submitting">
              <i class="icon-play"></i>
              ${ ui.message("reportingui.runButtonLabel") }
          </button>
        </div>
    </div>


</form>

