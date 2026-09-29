export interface IdsParam {
    ids: string[];
}

export interface OrderEntityParam {
    id: string;
    tabIndex: number;
}

export interface OrderEntityListParam {
    orderEntityList: OrderEntityParam[];
}

export interface OpinionFrameParam {
    processSerialNumber: string;
    opinionFrameMark: string;
    itemId: string;
    itembox: string;
    taskId?: string;
    taskDefinitionKey?: string;
}

export interface OpinionParam {
    id?: string;
    processSerialNumber: string;
    opinionFrameMark: string;
    content: string;
    processInstanceId?: string;
    taskId?: string;
}

export interface ForwardingParam {
    itemId: string;
    processSerialNumber: string;
    routeToTaskId: string;
    userChoice: UserChoiceParam[];
    taskId?: string;
    sponsorHandle?: string;
    sponsorGuid?: string;
}

export interface UserChoiceParam {
    id: string;
    type: string;
    sponsor: boolean;
}

export interface UserChoiceListParam {
    userChoice: UserChoiceParam[];
}

export interface SmsParam {
    processSerialNumber: string;
    send?: boolean;
    sign?: boolean;
    content?: string;
    positionIds?: string;
}
