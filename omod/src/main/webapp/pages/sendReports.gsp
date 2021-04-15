<%
    ui.decorateWith("appui", "standardEmrPage", [title: ui.message("Send Reports Page")])
    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("reportingui", "runReport.js")

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

<div>
    <label style="text-align: center"><h1>Send EMR Reports to DHIS2 </h1></label>

</div>

<%
    def renderingOptions = reportDefinitions
            .collect {
                [ value: it.uuid, label: ui.message(it.name) ]
            }
%>
<div class="row">
    <div class="col-md-6">
        <form method="post" id="sendReports">
            <fieldset>
                <legend> Run the Report</legend>
                ${ui.includeFragment("uicommons","field/dropDown",[
                        formFieldName: "reportDefinition",
                        label: "Report",
                        hideEmptyLabel: false,
                        options: renderingOptions

                ])}

                ${ ui.includeFragment("uicommons", "field/datetimepicker", [
                        formFieldName: "startDate",
                        label: "StartDate",
                        useTime: false,
                        defaultDate: ""
                ])}
                ${ ui.includeFragment("uicommons", "field/datetimepicker", [
                        formFieldName: "endDate",
                        label: "EndDate",
                        useTime: false,
                        defaultDate: ""
                ])}

                <p></p>
                    <span>
                        <button type="submit" class="confirm right" ng-class="{disabled: submitting}" ng-disabled="submitting">
                            <i class="icon-play"></i>
                            Send Report
                        </button>
                    </span>

            </fieldset>


        </form>
    </div>

</div>

