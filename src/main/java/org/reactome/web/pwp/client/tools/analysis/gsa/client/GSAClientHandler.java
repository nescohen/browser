package org.reactome.web.pwp.client.tools.analysis.gsa.client;

import org.reactome.web.pwp.client.tools.analysis.gsa.client.model.raw.*;

import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public interface GSAClientHandler {

    void onError(GSAError error);
    void onException(String msg);

    interface GSAMethodsHandler extends GSAClientHandler {
        void onMethodsSuccess(List<Method> methods);
    }

    interface GSADatasetTypesHandler extends GSAClientHandler {
        void onTypesSuccess(List<DatasetType> types);
    }

    interface GSAExampleDatasetsHandler extends GSAClientHandler {
        void onExampleDatasetSuccess(List<ExampleDataset> examples);
    }

    interface GSAExternalDatasourcesHandler extends GSAClientHandler {
        void onExternalDatasourcesSuccess(List<ExternalDatasource> externalDatasources);
    }

    interface GSADatasetLoadHandler extends GSAClientHandler {
        void onDatasetLoadSuccess(String statusToken);
    }

    interface GSADatasetSummaryHandler extends GSAClientHandler {
        void onDatasetSummarySuccess(ExampleDatasetSummary summary);
    }

    interface GSAStatusHandler extends GSAClientHandler {
        void onStatusSuccess(Status status);
    }

    interface GSAAnalysisHandler extends GSAClientHandler {
        void onAnalysisSubmissionSuccess(String gsaToken);
    }

    interface GSAResultLinksHandler extends GSAClientHandler {
        void onResultLinksSuccess(ResultLinks resultLinks);
    }

    interface GSAReportsStatusHandler extends GSAClientHandler {
        void onReportsStatusSuccess(Status reportsStatus);
    }
}
