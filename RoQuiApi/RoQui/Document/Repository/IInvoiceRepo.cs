namespace RoQuiApi.RoQui.Invoice.Repository;

using RoQuiApi.RoQui.Invoice.Model;

public interface IInvoiceRepo
{
    Document? GetDocumentByCodeAndNumber(string code, string number);

    void DeleteDocument(Document document);
    void CreateInvoice(Document invoice);
    bool SaveChanges();
}