<%
    def breadcrumbMiddle = breadcrumbOverride ?: '';
%>
<style type="text/css">
.grey {
    background-color:#c7c5c5 !important;
}

/* Tooltip container */
.tooltips {
    position: relative;
    display: inline-block;
}

/* Tooltip text */
.tooltips .tooltiptext {
    visibility: hidden;
    left: 0;
    top: 110%;
    width: 200px;
    background-color:#c7c5c5;
    color: #000000;
    text-align: center;
    padding: 5px 0;
    border-radius: 6px;
    text-wrap: none !important;
    position: absolute;
    z-index: 1
}

/* Show the tooltip text when you mouse over the tooltip container */
.tooltips:hover .tooltiptext {
    visibility: visible;
}
</style>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("coreapps.app.systemAdministration.label")}", link: '/' + OPENMRS_CONTEXT_PATH + '/coreapps/systemadministration/systemAdministration.page'},
        { label: "UgandaEMR Sync", link: '/' + OPENMRS_CONTEXT_PATH + '/ugandaemrsync/ugandaemrsync.page'},
        { label: "Send Reports"}
    ];

    var previewBody;
    var uuid;

    function clearDisplayReport(){
        jq("#display-report").empty();
        jq('#submit-button').empty();
    }
    function displayMERReport(report){
        var reportDataString="";
        var tableHeader = "<table><thead><tr><th>Indicator Code</th><th>Indicator Name and Disaggregations </th>";
        var endTableHeader ="</thead><tbody>";
        var tableFooter = "</tbody></table>";
        var ageHeaders="";
        var sexHeaders="";
        var total_Value ="";
        var dataElementsNo;
        var firstSexPosition="";

        jq.each(report.group, function (index, rowValue) {
            var indicatorCode="";
            var total_Display_Name="";
            var dataValueToDisplay = "";
            dataValueToDisplay += "<tr>";

                indicatorCode = rowValue.code.coding[0].code;
                var indicatorDisplay = rowValue.code.coding[0].display;
                total_Value = rowValue.measureScore.value;
                // total_Display_Name = rowValue.stratifier[0].code[0].coding[0].display;



            if(rowValue.stratifier.length!==0) {
                var disaggregated_rows = rowValue.stratifier[0].stratum;
                if(disaggregated_rows.length>1){
                    var rowspanAttribute="rowspan= \""+((disaggregated_rows.length)/2)+"\"";
                }

                dataValueToDisplay += "<td "+ rowspanAttribute+ ">"+indicatorCode+ "<span>"+indicatorDisplay+"</span>"+"</td>";
                jq.each(disaggregated_rows, function (key, obj) {
                    var row_displaySexKey = "";
                    var row_displaySexName = "";
                    var row_displayDisaggregateName = "";
                    jq.each(obj.component,function(k,v){
                       if(v.code.coding[0].code=='SEX'){
                           row_displaySexKey = v.value.coding[0].code;
                           row_displaySexName = v.value.coding[0].display;
                       }else{
                           row_displayDisaggregateName = v.value.coding[0].display;
                       }
                    });

                    var row_displayValue = obj.measureScore.value;

                    var row ="";

                    if (index === 0 && key===0) {
                        firstSexPosition = row_displaySexKey;
                    }
                    if (index === 0 && (key===1 || key===0)){
                            sexHeaders = sexHeaders + "<th>" + row_displaySexName + "</th>";
                    }
                    if(key%2===0 && key!==0){
                        row+="<tr>";
                    }
                    if(row_displaySexKey === firstSexPosition){
                        row+= "<td>" + row_displayDisaggregateName + "</td><td>"+row_displayValue+"</td>";
                    }else{
                        row+= "<td>" + row_displayValue + "</td></tr>";
                    }
                    dataValueToDisplay += row;
                });
            }else{
                dataValueToDisplay+="</tr>";
            }


            reportDataString += dataValueToDisplay;
        });

        tableHeader+=sexHeaders;

        jq("#display-report").append(tableHeader+endTableHeader + reportDataString + tableFooter);
        jq('#submit-button').show();
    }

    function displayHMISReport(report){
        var reportDataString="";
        var tableHeader = "<table><thead><tr rowspan='2'><th>Data element</th>";
        var sexRow = "<tr><th></th>";
        var endTableHeader ="</thead><tbody>";
        var tableFooter = "</tbody></table>";
        var ageHeaders="";
        var sexHeaders="";
        var total_Value ="";
        var dataElementsNo;

        jq.each(report.group, function (index, rowValue) {
            var indicatorCode="";
            var total_Display_Name="";
            var dataValueToDisplay = "";
            dataValueToDisplay += "<tr>";



            indicatorCode = rowValue.code.coding[0].code;
            var indicatorDisplay = rowValue.code.coding[0].display;
            // total_Display_Name = rowValue.stratifier[0].code[0].coding[0].display;
             total_Value = rowValue.measureScore.value;
            dataValueToDisplay += "<td class='tooltips'>"+indicatorCode+ "<span class='tooltiptext'>"+indicatorDisplay+"</span>"+"</td>";

            if(rowValue.stratifier.length!==0) {
                var disaggregated_rows = rowValue.stratifier[0].stratum;

                jq.each(disaggregated_rows, function (key, obj) {
                    var row_displayAgeKey = obj.component[0].value.coding[0].code;
                    var row_displaySexKey = obj.component[1].value.coding[0].code;
                    var row_displayValue = obj.measureScore.value;
                    var row_displayAgeName = obj.component[0].value.coding[0].display;

                    if (index == 0) {
                        if (key % 2) {
                            ageHeaders = ageHeaders + "<th colspan='2'>" + row_displayAgeName + "</th>";
                        }
                        sexHeaders = sexHeaders + "<th>" + row_displaySexKey + "</th>";
                        dataElementsNo = disaggregated_rows.length;
                    }

                    dataValueToDisplay += "<td>" + row_displayValue + "</td>";

                });
            }else{
                if(typeof dataElementsNo !=="undefined"){
                    for(var x=1;x<=dataElementsNo;x++){
                        dataValueToDisplay += "<td class='grey'></td>";
                    }
                }
            }

                dataValueToDisplay += "<td>" + total_Value + "</td>";

            dataValueToDisplay += "</tr>";
            reportDataString += dataValueToDisplay;
        });
        ageHeaders = ageHeaders + "<th></th>";
        sexHeaders= sexHeaders+ "<th>Total</th>";

        tableHeader +=ageHeaders +"</tr>";
        sexRow+=sexHeaders + "</tr>";
        tableHeader+=sexRow;

        jq("#display-report").append(tableHeader+endTableHeader + reportDataString + tableFooter);
        jq('#submit-button').show();

    }

    function sendPayLoadInPortionsWithIndicators(dataObject,chunkSize){
        var objectsToSend =[];
        var groupArrayLength = dataObject.group.length;

        if(groupArrayLength % chunkSize===0){
            var myArray = dataObject.group;

            var setNumber = groupArrayLength/chunkSize;
            for (var i=0,len=myArray.length; i<len; i+=chunkSize){
                var slicedArray = myArray.slice(i,i+chunkSize);
                delete dataObject.group;
                var reportObject =Object.assign({},dataObject);
                reportObject.group =  myArray.slice(i,i+chunkSize);
                objectsToSend.push(reportObject);
            }
        }
        return objectsToSend;
    }

    function stripDisplayAttributes(dataObject){
        var arrayLength = dataObject.group.length;
        if(arrayLength > 0){
            var myArray = dataObject.group;

            for (var i=0; i < myArray.length; i++) {
                var myObject = myArray[i];
                var attr1 = myObject.code.coding;
                attr1 = attr1.map(u=>({"code":u.code}));
                myArray[i].code.coding=attr1;

                var attr2 = myObject.stratifier[0];
                var attr2Child =attr2.code
                if(attr2Child.length>0){
                    for(var x=0; x < attr2Child.length;x++){
                        var myObject = attr2Child[x];
                        var child = myObject.coding;
                        child = child.map(u =>({"code":u.code}));
                        attr2Child[x].coding = child;
                    }
                }
                myArray[i].stratifier[0].code=attr2Child;


                var attr2Child1 =attr2.stratum;
                if(attr2Child1.length>0){
                    for(var k=0; k < attr2Child1.length;k++){
                        var myObject = attr2Child1[k];
                        if(typeof myObject.value == "undefined"){
                            var componentObject = myObject.component
                            if(componentObject.length>0){
                                for(var j=0; j < componentObject.length;j++){
                                    var child = componentObject[j].value.coding;
                                    child = child.map(u =>({"code":u.code}));
                                    attr2Child1[k].component[j].value.coding = child;
                                }

                            }


                        }else{
                            var child = myObject.value.coding;
                            child = child.map(u =>({"code":u.code}));
                            attr2Child1[k].value.coding = child;
                        }

                    }
                }
                myArray[i].stratifier[0].stratum=attr2Child1;

            }
            dataObject.group = []
            dataObject.group= myArray;
        }
        return dataObject;

    }

    function post(url, dataObject) {
        jq("#loader").show();
        return jq.ajax({
            method: 'POST',
            url: url,
            data: jQuery.param(dataObject),
            headers: {'Content-Type': 'application/json; charset=utf-8'}
        });
    }

    function sendData(jsonData,urlEndPoint) {

        jq.ajax({
            url:'${ui.actionLink("ugandaemrsync","sendReports","sendData")}',
            type: "POST",
            data: {body:jsonData,
                    uuid:urlEndPoint},
            dataType:'json',

            beforeSend : function()
            {
                jq("#loader").show();
            },
            success: function (data) {
                var response = data;
                console.log(response);
                if (data.status === "success") {
                    jq().toastmessage('showSuccessToast', response.message);
                    clearDisplayReport();
                } else {
                    jq().toastmessage('showErrorToast', response.message);
                }
                jq("#loader").hide();
            }
        });
    }
    jq(document).ready(function () {
        previewBody =${previewBody};
        uuid ="${reportuuid}";

        jq("#loader").hide();
        jq("#submit-button").css('display', 'none');
        var errorMessage = jq('#errorMessage').val();

        if(errorMessage!==""){
            jq().toastmessage('showNoticeToast', errorMessage);
        }

        jq('#sendData').click(function(){
            var strippedPreviewBody = stripDisplayAttributes(previewBody);
            var data = sendPayLoadInPortionsWithIndicators(strippedPreviewBody,3);
            // data = JSON.stringify(previewBody,null,0);
             data = JSON.stringify(data);
            sendData(data,uuid);
        });

       if(previewBody!=null){
           displayMERReport(previewBody);
           // displayHMISReport(previewBody);
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
<div>
    <button type="button" style="font-size: 25px" class="confirm" data-toggle="modal"
            data-target="#run-report"  data-whatever="@mdo"> Run a report</button>
</div>
<div class="row">

    <div class="col-md-12">
        <div id="loader">
            <img src="/openmrs/ms/uiframework/resource/uicommons/images/spinner.gif">
        </div>
        <div id="display-report" style="overflow-y:scroll;">
            <div class='modal-header'> <label style="text-align: center"><h1> ${report_title}</h1></label></div>
        </div>
        <div id="submit-button">
            <p></p><span id="sendData"  class="button confirm right"> Submit </span>
        </div>
    </div>

</div>

<div class="modal fade" id="run-report" tabindex="-1" role="dialog"
     aria-labelledby="addEditSyncTaskTypeModelLabel"
     aria-hidden="true">
    <div class="modal-dialog  modal-md" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="addEditSyncTaskTypeModelLabel">Run the Report</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div>
                <form method="post" id="sendReports">
                    <fieldset>
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
                                Run
                            </button>
                        </span>

                    </fieldset>
                    <input type="hidden" name="errorMessage" id="errorMessage" value="${errorMessage}">
                </form>
            </div>
        </div>
    </div>
</div>


