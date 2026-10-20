using Xunit.Abstractions;
using Xunit.Sdk;

[assembly: CollectionBehavior(DisableTestParallelization = true)]
[assembly: TestCollectionOrderer(
    ordererTypeName: "RoQuiApi.Test.DisplayNameOrderer",
    ordererAssemblyName: "RoQuiApi.Test")]

namespace RoQuiApi.Test;

public class DisplayNameOrderer : ITestCollectionOrderer
{
    public IEnumerable<ITestCollection> OrderTestCollections(
        IEnumerable<ITestCollection> testCollections
    ) => testCollections.OrderBy(collection => collection.DisplayName);
}

public class MethodNameOrderer : ITestCaseOrderer
{
    public IEnumerable<TTestCase> OrderTestCases<TTestCase>(
        IEnumerable<TTestCase> testCases
    ) where TTestCase : ITestCase
        => testCases.OrderBy(testCase => testCase.TestMethod.Method.Name);
}

[CollectionDefinition("01_Taxpayer")]
public class TaxpayerCollection;

[CollectionDefinition("02_Invoice")]
public class InvoiceCollection;

[CollectionDefinition("03_CreditNote")]
public class CreditNoteCollection;

[CollectionDefinition("04_DebitNote")]
public class DebitNoteCollection;

[CollectionDefinition("05_Liquidation")]
public class LiquidationCollection;

[CollectionDefinition("06_Withhold")]
public class WithholdCollection;

[CollectionDefinition("07_Delivery_Note")]
public class DeliveryNoteCollection;