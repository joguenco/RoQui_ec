namespace RoQuiApi.RoQui.Document.Repository;

using RoQuiApi.RoQui.Document.Model;

public interface IDocumentRepo
{
    Document? GetDocumentByCodeAndNumber(string code, string number);

    void DeleteDocument(Document document);
    void CreateInvoice(Document invoice);
    bool SaveChanges();
}