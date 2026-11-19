import { Bug, Lightbulb, ListChecks } from 'lucide-react'

export const TIPOS = {
  TAREFA: { nome: 'Tarefa', Icone: ListChecks, detalhe: 'Critérios de aceite' },
  BUG: { nome: 'Bug', Icone: Bug, detalhe: 'Passos para reprodução' },
  MELHORIA: { nome: 'Melhoria', Icone: Lightbulb, detalhe: 'Justificativa' },
}
