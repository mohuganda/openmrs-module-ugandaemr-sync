<div id="apps">
    <% if (requestFacility == true) { %>
    <a id="ugandaemrsync-get-facility-id" href="/openmrs/ugandaemrsync/getFacility.page" class="button app big">

        <i class="icon-search"></i>

        Request Facility ID
    </a>
    <% } %>

    <% if (requestFacility == false) { %>

    <a id="ugandaemrsync-generate-initial-data" href="/openmrs/ugandaemrsync/initialDataGeneration.page"
       class="button app big">

        <i class="icon-search"></i>

        Start Synchronization
    </a>
    <% } %>
</div>