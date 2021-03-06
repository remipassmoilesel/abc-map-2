
export class DrawingTool {

  constructor(public id: string,
              public icon: string,
              public name: string) {}

}

export class DrawingTools {

  // TODO: find better icons
  public static readonly Point = new DrawingTool('Point', 'fa-circle', 'Point');
  public static readonly LineString = new DrawingTool('LineString', 'fa-slash', 'Ligne');
  public static readonly Polygon = new DrawingTool('Polygon', 'fa-draw-polygon', 'Polygone');
  public static readonly None = new DrawingTool('None', 'fa-times', 'Aucun');

  public static readonly All = [
    DrawingTools.Point,
    DrawingTools.LineString,
    DrawingTools.Polygon,
    DrawingTools.None
  ];
}
