export class TableObject {
    data: any[] = [];
    currentPage = 1;
    total = 0;
    pageSize = 2;
    pageSizes: number[] = [2, 20, 30, 50];
    height?: number;
    multipleSelection: any[] = [];
}
