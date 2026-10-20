export interface CampoInvalido {
  campo: string;
  mensagem: string;
}

export interface ProblemDetail {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  instance?: string;
  timestamp?: string;
  campos?: CampoInvalido[];
}
