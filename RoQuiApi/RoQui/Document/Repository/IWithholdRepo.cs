namespace RoQuiApi.RoQui.Document.Repository;

using RoQuiApi.RoQui.Document.Model;

public interface IWithholdRepo
{
    Withhold? GetWithholdByCodeAndNumber(string code, string number);

    void DeleteWithhold(Withhold withhold);
    void CreateWithhold(Withhold withhold);
    bool SaveChanges();
}
