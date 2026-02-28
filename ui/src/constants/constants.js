export const MONDAY = [
	{
		text: 'Понедельник',
		align: 'center',
		sortable: false,
		value: 'value'
	},
]

export const TUESDAY = [
	{
		text: 'Вторник',
		align: 'center',
		sortable: false,
		value: 'value'
	},
]

export const WEDNESDAY = [
	{
		text: 'Среда',
		align: 'center',
		sortable: false,
		value: 'value'
	},
]

export const THURSDAY = [
	{
		text: 'Четверг',
		align: 'center',
		sortable: false,
		value: 'value'
	},
]

export const FRIDAY = [
	{
		text: 'Пятница',
		align: 'center',
		sortable: false,
		value: 'value'
	},
]

export const SATURDAY = [
	{
		text: 'Суббота',
		align: 'center',
		sortable: false,
		value: 'value'
	},
]

export const TIME = [
	{
		text: 'Время',
		align: 'center',
		sortable: false,
		value: 'value'
	},
]

export default function getRowColor(item) {
	if (item.disciplineType === null) {
		return;
	}
	switch (item.disciplineType.key) {
		case 'Лекция':
			return 'discipline_type_715'
		case 'Лабораторная работа 1':
		case 'Лабораторная работа 2':
			return 'discipline_type_716_717';
		case 'Семинар':
			return 'discipline_type_718';
		case 'Экзамен':
			return 'discipline_type_719';
		case 'Зачет':
			return 'discipline_type_720';
		case 'Курсовая работа':
			return 'discipline_type_721';
		case 'Курсовой проект':
			return 'discipline_type_722';
		default:
			return;
	}
}

export function arrayToUriParams(array, key) {
	return array.map(t => `${key}=${encodeURIComponent(t)}`).join('&')
}